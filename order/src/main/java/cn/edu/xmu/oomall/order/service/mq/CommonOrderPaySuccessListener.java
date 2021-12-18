package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.microservice.GoodsService;
import cn.edu.xmu.oomall.order.microservice.bo.PaymentState;
import cn.edu.xmu.oomall.order.microservice.vo.QuantityVo;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.po.OrderItemPo;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.service.mq.vo.NotifyMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * 普通订单 团购 秒杀
 *
 * @author xiuchen lang 22920192204222
 * @date 2021/12/17 15:56
 */
@Service
@RocketMQMessageListener(topic = "${oomall.order.pay.common}", consumerGroup = "${oomall.order.pay.common}")
public class CommonOrderPaySuccessListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;
    @Autowired
    GoodsService goodsService;

    @Override
    public void onMessage(String message) {
        NotifyMessage notifyMessage = JacksonUtil.toObj(message, NotifyMessage.class);
        if (!notifyMessage.getState().equals(PaymentState.ALREADY_PAY.getCode())) {
            return;
        }
        ReturnObject orderByOrderSn = orderDao.getOrderByOrderSn(notifyMessage.getDocumentId());
        if (orderByOrderSn.getCode() != ReturnNo.OK) {
            return;
        }
        OrderPo orderPo = (OrderPo) orderByOrderSn.getData();
        Order order = cloneVo(orderPo, Order.class);
        ReturnObject orderItemListReturnObject = orderDao.listOrderItemsByOrderId(order.getId());
        if (orderItemListReturnObject.getCode() != ReturnNo.OK) {
            return;
        }
        List<OrderItemPo> orderItemPos = (List<OrderItemPo>) orderItemListReturnObject.getData();
        for (OrderItemPo orderItemPo : orderItemPos) {
            //减少库存
            goodsService.decreaseOnSale(orderItemPo.getShopId(), orderItemPo.getOnsaleId(), new QuantityVo(-orderItemPo.getQuantity()));
        }
        if (order.getGrouponId() != 0) {
            //这是团购，不用拆单子
            if (order.getState() != OrderState.NEW_ORDER.getCode()) {
                return;
            }
            order.setState(OrderState.WAIT_GROUP.getCode());
            setPoModifiedFields(order, 0L, null);
            orderDao.updateOrder(order);
            return;
        }
        //普通订单
        if (orderPo.getState() != OrderState.NEW_ORDER.getCode()) {
            return;
        }
        //查明细列表
        ReturnObject<List<OrderItem>> returnObject = orderDao.listOrderItemsByOrderId(orderPo.getId());
        if (returnObject.getCode() != ReturnNo.OK) {
            return;
        }
        List<OrderItem> orderItemList = returnObject.getData();
        Set<Long> set = new HashSet<>();
        for (OrderItem orderItem : orderItemList) {
            set.add(orderItem.getShopId());
        }
        if (set.size() <= 1) {
            order.setState(OrderState.FINISH_PAY.getCode());
            setPoCreatedFields(order, 0L, null);
            orderDao.updateOrder(order);
            return;
        }
        //分单逻辑
        //父订单状态改为已分单
        order.setState(OrderState.NOT_FROUP.getCode());
        setPoCreatedFields(order, 0L, null);
        orderDao.updateOrder(order);

        Long discountPrice = 0L;
        Long originprice = 0L;
        Long point = 0L;
        for (Long shopId : set) {
            discountPrice = 0L;
            originprice = 0L;
            point = 0L;
            List<OrderItem> listByShop = new ArrayList();
            //上面的order就当新的子order
            order.setPid(order.getId());
            order.setId(null);
            order.setExpressFee(null);
            order.setShopId(shopId);
            //子订单生成订单号
            order.setOrderSn(genSeqNum(1));
            setPoCreatedFields(order, 0L, null);
            setPoModifiedFields(order, 0L, null);
            for (OrderItem orderItem : orderItemList) {
                //是这个商铺的  累加积点，累加钱，累加优惠 子改pid，改状态    改明细order_id
                if (orderItem.getShopId().equals(shopId)) {
                    listByShop.add(orderItem);
                    discountPrice += orderItem.getDiscountPrice() * orderItem.getQuantity();
                    originprice += orderItem.getPrice() * orderItem.getQuantity();
                    point += orderItem.getPoint() * orderItem.getQuantity();
                }
            }
            order.setDiscountPrice(discountPrice / 10);
            order.setPoint(point / 10);
            order.setOriginPrice(originprice);
            ReturnObject returnObject1 = orderDao.insertOrder(order);
            if (returnObject1.getCode() != ReturnNo.OK) {
                return;
            }
            OrderPo data = (OrderPo) returnObject1.getData();
            for (OrderItem orderItem : listByShop) {
                orderItem.setOrderId(data.getId());
                orderDao.insertOrderItem(orderItem);
            }
            return;
        }
    }
}
