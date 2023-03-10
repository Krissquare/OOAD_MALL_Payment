package cn.edu.xmu.oomall.ordermq.service.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ordermq.dao.OrderDao;
import cn.edu.xmu.oomall.ordermq.model.bo.Order;
import cn.edu.xmu.oomall.ordermq.model.bo.OrderItem;
import cn.edu.xmu.oomall.ordermq.model.po.OrderPo;
import cn.edu.xmu.oomall.ordermq.model.vo.OrderAndOrderItemsVo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/12 17:10
 * topic 对应send的destination
 */
@Service
@RocketMQMessageListener(topic = "${oomall.order.insert}",consumerGroup ="${oomall.order.insert}",consumeThreadMax = 128)
public class InsertOrderListener implements RocketMQListener<String>
{
    @Autowired
    OrderDao orderDao;
    @Override
    public void onMessage(String message) {
        OrderAndOrderItemsVo orderAndOrderItemsVo = JacksonUtil.toObj(message, OrderAndOrderItemsVo.class);
        Order order = orderAndOrderItemsVo.getOrder();
        List<OrderItem> orderItems = orderAndOrderItemsVo.getOrderItems();
        ReturnObject returnObject = orderDao.insertOrder(order);
        if (returnObject.getCode()== ReturnNo.OK){
            OrderPo orderPo = (OrderPo) returnObject.getData();
            for (OrderItem orderItem:orderItems){
                orderItem.setOrderId(orderPo.getId());
                orderDao.insertOrderItem(orderItem);
            }
        }
    }
}
