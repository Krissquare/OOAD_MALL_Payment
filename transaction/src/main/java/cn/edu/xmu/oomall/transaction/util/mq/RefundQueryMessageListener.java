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
@RocketMQMessageListener(topic = "refund-query-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "refund-query-group")
public class RefundQueryMessageListener implements RocketMQListener<String> {

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    @Override
    public void onMessage(String message) {
        RefundQueryMessage refundQueryMessage = JSONObject.parseObject(message, RefundQueryMessage.class);
        TransactionPattern pattern = transactionPatternFactory.
                getPatternInstance(refundQueryMessage.getRefundBill().getRelatedPayment().getPatternId());
        pattern.queryRefund(refundQueryMessage.getRefundBill());
    }

}
