package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
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
 * @author xiuchen lang 22920192204222
 * @date 2021/12/13 21:28
 */
@Service
@RocketMQMessageListener(topic = "pay-success", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "${rocketmq.consumer.group}")
public class PaySuccessListener implements RocketMQListener<String> {

    @Autowired
    OrderDao orderDao;

    @Override
    public void onMessage(String message) {
        //TODO: message类型 假设只返回成功 暂且都当写入数据库 没写入数据库的还没有考虑
        ReturnObject orderByOrderSn = orderDao.getOrderByOrderSn(message);
        if (orderByOrderSn.getCode() != ReturnNo.OK) {
            return;
        }
        //有这个订单
        OrderPo orderPo = (OrderPo) orderByOrderSn.getData();
        Order order = cloneVo(orderPo, Order.class);
        if (order.getGrouponId() != 0) {
            //这是团购，不用拆单子
            if (order.getState() != OrderState.NEW_ORDER.getCode()) {
                return;
            }
            order.setState(OrderState.WAIT_GROUP.getCode());
            setPoCreatedFields(order, 0L, null);
            orderDao.updateOrder(order);
            return;
        } else if (order.getAdvancesaleId() != 0) {
            //这是预售 不用拆单子
            if (order.getState() == OrderState.NEW_ORDER.getCode()) {
                //定金
                order.setState(OrderState.WAIT_PAY_REST.getCode());
            } else if (order.getState() == OrderState.WAIT_PAY_REST.getCode()) {
                //尾款
                order.setState(OrderState.FINISH_PAY.getCode());
            }else {
                return;
            }
            setPoCreatedFields(order, 0L, null);
            orderDao.updateOrder(order);
            return;
        }
        //普通订单
        if (orderPo.getState() != OrderState.NEW_ORDER.getCode()) {
            return;
        }
        order.setState(OrderState.FINISH_PAY.getCode());
        setPoCreatedFields(order, 0L, null);
        orderDao.updateOrder(order);
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
            return;
        }
        //分单逻辑
        Long discountPrice=0L;
        Long originprice=0L;
        Long point=0L;
        for (Long shopId : set) {
            discountPrice=0L;
            originprice=0L;
            point=0L;
            List<OrderItem> listByShop = new ArrayList();
            //上面的order就当新的子order
            order.setPid(order.getId());
            order.setId(null);
            order.setExpressFee(null);
            order.setShopId(shopId);
            //TODO:子订单生成订单号
            setPoCreatedFields(order,0L,null);
            setPoModifiedFields(order,0L,null);
            for (OrderItem orderItem : orderItemList) {
                //是这个商铺的  累加积点，累加钱，累加优惠 子改pid，改状态    改明细order_id
                if (orderItem.getShopId().equals(shopId)) {
                    listByShop.add(orderItem);
                    discountPrice+=orderItem.getDiscountPrice()*orderItem.getQuantity();
                    originprice+=orderItem.getPrice()*orderItem.getQuantity();
                    point+=orderItem.getPoint()*orderItem.getQuantity();
                }
            }
            order.setDiscountPrice(discountPrice/10);
            order.setPoint(point/10);
            order.setOriginPrice(originprice);
            ReturnObject returnObject1 = orderDao.insertOrder(order);
            if (returnObject1.getCode()!=ReturnNo.OK){
                return;
            }
            OrderPo data = (OrderPo) returnObject1.getData();
            for (OrderItem orderItem:listByShop){
                orderItem.setOrderId(data.getId());
                orderDao.insertOrderItem(orderItem);
            }
            return;
        }
    }
}
