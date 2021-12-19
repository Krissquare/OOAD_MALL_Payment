package cn.edu.xmu.oomall.transaction.util;

import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundBill {

    private Long paymentId;

    private String documentId;

    private Byte documentType;

    private String descr;

    private Long amount;

    private String reason;

    private Refund relatedRefund;

    private Payment relatedPayment;

    public String getOutTradeNo()
    {
        return TransactionPatternFactory.encodeRequestNo(relatedPayment.getId(),
                relatedPayment.getDocumentId(), relatedPayment.getDocumentType());
    }

    public String getOutRefundNo()
    {
        return TransactionPatternFactory.encodeRequestNo(relatedRefund.getId(),
                relatedRefund.getDocumentId(), relatedRefund.getDocumentType());
    }

}
