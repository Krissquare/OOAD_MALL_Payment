package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.order.dao.OrderDao;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/14 9:25
 */
@Service
@RocketMQMessageListener(consumerGroup = "${oomall.refund.order.deposittopic}", topic = "${oomall.refund.order.deposittopic}")
public class AdvanceDepositRefundSuccessListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;

    @Override
    public void onMessage(String message) {
        //因为退款成不成功与订单无关所以不处理
        return;
    }
}
