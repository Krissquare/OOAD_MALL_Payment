package cn.edu.xmu.oomall.transaction.util.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
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

@Service
@RocketMQMessageListener(topic = "refund-query-topic", consumeMode = ConsumeMode.ORDERLY, consumerGroup = "refund-query-group")
public class RefundQueryMessageListener implements RocketMQListener<String> {

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    private static RefundQueryMessage getInstance(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(message);
        RefundBill refundBill = new RefundBill();
        JsonNode refundBillNode = root.get("refundBill");
        String relatedPaymentString = refundBillNode.get("relatedPayment").toString();
        String relatedRefundString = refundBillNode.get("relatedRefund").toString();
        refundBill.setPaymentId(refundBillNode.get("paymentId").asLong());
        refundBill.setDocumentId(refundBillNode.get("documentId").asText());
        refundBill.setDocumentType(Byte.parseByte(refundBillNode.get("documentType").toString()));
        refundBill.setAmount(refundBillNode.get("amount").asLong());
        refundBill.setDescr(refundBillNode.get("descr").toString());
        refundBill.setReason(refundBillNode.get("reason").toString());
        refundBill.setRelatedPayment(JacksonUtil.toObj(relatedPaymentString, Payment.class));
        refundBill.setRelatedRefund(JacksonUtil.toObj(relatedRefundString, Refund.class));

        RefundQueryMessage refundQueryMessage = new RefundQueryMessage();
        refundQueryMessage.setRefundBill(refundBill);
        return refundQueryMessage;
    }


    @SneakyThrows
    @Override
    public void onMessage(String message) {
        RefundQueryMessage refundQueryMessage = getInstance(message);
        TransactionPattern pattern = transactionPatternFactory.
                getPatternInstance(refundQueryMessage.getRefundBill().getRelatedPayment().getPatternId());
        pattern.queryRefund(refundQueryMessage.getRefundBill());
    }

}
