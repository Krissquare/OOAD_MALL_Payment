package cn.edu.xmu.oomall.ordermq.service.mq;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ordermq.dao.OrderDao;
import cn.edu.xmu.oomall.ordermq.microservice.InternalGoodsService;
import cn.edu.xmu.oomall.ordermq.service.mq.bo.PaymentState;
import cn.edu.xmu.oomall.ordermq.microservice.vo.IntegerQuantityVo;
import cn.edu.xmu.oomall.ordermq.model.bo.Order;
import cn.edu.xmu.oomall.ordermq.model.bo.OrderItem;
import cn.edu.xmu.oomall.ordermq.model.bo.OrderState;
import cn.edu.xmu.oomall.ordermq.model.po.OrderPo;
import cn.edu.xmu.oomall.ordermq.service.mq.vo.PaymentNotifyMessage;
import cn.edu.xmu.oomall.ordermq.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
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
@RocketMQMessageListener(topic = "${oomall.payment.order.commontopic}", consumerGroup = "${oomall.payment.order.commontopic}")
public class CommonOrderPaySuccessListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;
    @Autowired
    InternalGoodsService internalGoodsService;

    @Override
    public void onMessage(String message) {
        PaymentNotifyMessage paymentNotifyMessage = JSONObject.parseObject(message, PaymentNotifyMessage.class);
        if(!paymentNotifyMessage.getPaymentState().equals(PaymentState.ALREADY_PAY)){
            return;
        }
        ReturnObject orderByOrderSn = orderDao.getOrderByOrderSn(paymentNotifyMessage.getDocumentId());
        if (orderByOrderSn.getCode() != ReturnNo.OK) {
            return;
        }
        OrderPo orderPo = (OrderPo) orderByOrderSn.getData();
        Order order = cloneVo(orderPo, Order.class);
        ReturnObject orderItemListReturnObject = orderDao.listOrderItemsByOrderId(order.getId());
        if (orderItemListReturnObject.getCode() != ReturnNo.OK) {
            return;
        }
        List<OrderItem> orderItems = (List<OrderItem>) orderItemListReturnObject.getData();
        for (OrderItem orderItem : orderItems) {
            //减少库存
            internalGoodsService.updateOnsaleQuantity(orderItem.getOnsaleId(), new IntegerQuantityVo(Math.toIntExact(-orderItem.getQuantity())));
        }
        if (order.getGrouponId() != 0) {
            //这是团购，不用拆单子
            if (order.getState() != OrderState.NEW_ORDER.getCode()) {
                return;
            }
            order.setState(OrderState.WAIT_GROUP.getCode());
            setPoModifiedFields(order, order.getCreatorId(), order.getCreatorName());
            orderDao.updateOrder(order);
            return;
        }
        //普通订单
        if (orderPo.getState() != OrderState.NEW_ORDER.getCode()) {
            return;
        }
        //查明细列表
        Set<Long> set = new HashSet<>();
        for (OrderItem orderItem : orderItems) {
            set.add(orderItem.getShopId());
        }
        if (set.size() <= 1) {
            order.setState(OrderState.FINISH_PAY.getCode());
            setPoModifiedFields(order, order.getCreatorId(), order.getCreatorName());
            orderDao.updateOrder(order);
            return;
        }
        //分单逻辑
        //父订单状态改为已支付
        order.setState(OrderState.FINISH_PAY.getCode());
        setPoModifiedFields(order, order.getCreatorId(), order.getCreatorName());
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
            order.setOrderSn(IdUtil.getGuid());
            setPoCreatedFields(order, order.getCreatorId(), order.getCreatorName());
            setPoModifiedFields(order, order.getCreatorId(), order.getCreatorName());
            for (OrderItem orderItem : orderItems) {
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
        }
        return;
    }
}
