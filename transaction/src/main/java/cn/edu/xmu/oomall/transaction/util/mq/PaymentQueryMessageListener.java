package cn.edu.xmu.oomall.transaction.util.mq;

import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RocketMQMessageListener(topic = "payment-query-topic", consumeMode = ConsumeMode.ORDERLY, consumerGroup = "payment-query-group")
public class PaymentQueryMessageListener implements RocketMQListener<String> {

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    @Override
    public void onMessage(String message) {
        PaymentQueryMessage paymentQueryMessage = JSONObject.parseObject(message, PaymentQueryMessage.class);
        TransactionPattern pattern =
                transactionPatternFactory.getPatternInstance(paymentQueryMessage.getPaymentBill().getPatternId());
        pattern.queryPayment(paymentQueryMessage.getPaymentBill());
    }
}
