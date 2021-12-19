package cn.edu.xmu.oomall.transaction.util.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RocketMQMessageListener(topic = "payment-query-topic", consumeMode = ConsumeMode.ORDERLY, consumerGroup = "payment-query-group")
public class PaymentQueryMessageListener implements RocketMQListener<String> {

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    private static PaymentQueryMessage getInstance(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(message);
        PaymentBill paymentBill = new PaymentBill();
        JsonNode paymentJsonNode = root.get("paymentBill");
        String relatedPaymentString = paymentJsonNode.get("relatedPayment").toString();
        paymentBill.setPatternId(paymentJsonNode.get("pattern").asLong());
        paymentBill.setDocumentId(paymentJsonNode.get("documentId").toString());
        paymentBill.setDocumentType(Byte.parseByte(paymentJsonNode.get("documentType").toString()));
        paymentBill.setDescr(paymentJsonNode.get("descr").toString());
        paymentBill.setAmount(paymentJsonNode.get("amount").asLong());
        paymentBill.setBeginTime(JacksonUtil.toObj(paymentJsonNode.get("beginTime").toString(), LocalDateTime.class));
        paymentBill.setEndTime(JacksonUtil.toObj(paymentJsonNode.get("endTime").toString(), LocalDateTime.class));
        paymentBill.setRelatedPayment(JacksonUtil.toObj(relatedPaymentString, Payment.class));

        PaymentQueryMessage refundQueryMessage = new PaymentQueryMessage();
        refundQueryMessage.setPaymentBill(paymentBill);
        return refundQueryMessage;
    }

    @SneakyThrows
    @Override
    public void onMessage(String message) {
        PaymentQueryMessage paymentQueryMessage = getInstance(message);
        TransactionPattern pattern =
                transactionPatternFactory.getPatternInstance(paymentQueryMessage.getPaymentBill().getPatternId());
        pattern.queryPayment(paymentQueryMessage.getPaymentBill());
    }
}
