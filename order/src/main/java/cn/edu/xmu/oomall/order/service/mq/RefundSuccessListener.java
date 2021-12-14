package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/14 9:25
 */
@Service
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}", topic = "refund-success")
public class RefundSuccessListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;
    @Override
    public void onMessage(String documentId) {
        //TODO 假设只传documentId，跟据取消规则，只能取消未分单的订单 在退款那里（有102 201 202 三个状态有关）
        //更改父订单状态
        ReturnObject orderByOrderSn = orderDao.getOrderByOrderSn(documentId);
        if (orderByOrderSn.getCode()!= ReturnNo.OK){
            return;
        }
        OrderPo orderPo = (OrderPo) orderByOrderSn.getData();
        if (orderPo.getGrouponId()!=null){
        }
        //如果有子订单更改状态
        //退积点
        //退优惠券
        //添加库存

    }
}
