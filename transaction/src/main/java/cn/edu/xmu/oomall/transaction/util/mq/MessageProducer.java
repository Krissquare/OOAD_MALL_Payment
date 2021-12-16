package cn.edu.xmu.oomall.transaction.util.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    public void sendNotifyMessage(NotifyMessage notifyMessage) {
        String json = JacksonUtil.toJson(notifyMessage);
        Message message = (Message) MessageBuilder.withPayload(json).build();
        String topic = String.format("%s-%s", notifyMessage.getMessageType().getDescription(),
                notifyMessage.getDocumentType());
        rocketMQTemplate.sendOneWay(topic, message);
    }

    public void sendActiveQueryDelayedMessage(ActiveQueryMessage activeQueryMessage) {
        String json = JacksonUtil.toJson(activeQueryMessage);
        Message message = (Message) MessageBuilder.withPayload(json).build();
        // 30s
        // 主题名字考虑采用PV注入
        message.setDelayTimeLevel(4);
        rocketMQTemplate.sendOneWay("active-query", message);
    }

}

