package cn.edu.xmu.oomall.transaction.util.mq;


import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "active-query", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "active-query-group")
public class ActiveQueryMessageListener  implements RocketMQListener<ActiveQueryMessage> {


    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    @Override
    public void onMessage(ActiveQueryMessage activeQueryMessage) {
        TransactionPattern pattern = transactionPatternFactory.getPatternInstance(activeQueryMessage.getPatternId());
        if (activeQueryMessage.getMessageType().equals(ActiveQueryMessage.QueryMessageType.QUERY_PAYMENT)) {
            pattern.queryPayment(activeQueryMessage.getRequestNo(), (PaymentBill) activeQueryMessage.getBill());
        } else if(activeQueryMessage.getMessageType().equals(ActiveQueryMessage.QueryMessageType.QUERT_REFUND)) {
            pattern.queryRefund(activeQueryMessage.getRequestNo(), (RefundBill) activeQueryMessage.getBill());
        }
    }
}
