package cn.edu.xmu.oomall.transaction.util.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendPaymentNotifyMessage(PaymentNotifyMessage notifyMessage) {
        String json = JacksonUtil.toJson(notifyMessage);
        Message message = MessageBuilder.withPayload(json).build();
        String topic = String.format("payment-type%s-notify-topic", notifyMessage.getDocumentType());
        rocketMQTemplate.sendOneWay(topic, message);
    }

    public void sendRefundNotifyMessage(RefundNotifyMessage notifyMessage) {
        String json = JacksonUtil.toJson(notifyMessage);
        Message message = MessageBuilder.withPayload(json).build();
        String topic = String.format("refund-type%s-notify-topic", notifyMessage.getDocumentType());
        rocketMQTemplate.sendOneWay(topic, message);
    }

    public void sendPaymentQueryDelayedMessage(PaymentQueryMessage paymentQueryMessage) {
        String json = JacksonUtil.toJson(paymentQueryMessage);
        Message message =  MessageBuilder.withPayload(json).build();
        // 30s
        // 主题名字考虑采用PV注入

        rocketMQTemplate.sendOneWay("payment-query-topic", message);
    }

    public void sendRefundQueryDelayedMessage(RefundQueryMessage refundQueryMessage) {
        String json = JacksonUtil.toJson(refundQueryMessage);
        Message message =  MessageBuilder.withPayload(json).build();
        // 30s
        // 主题名字考虑采用PV注入
        rocketMQTemplate.sendOneWay("refund-query-topic", message);
    }

}

