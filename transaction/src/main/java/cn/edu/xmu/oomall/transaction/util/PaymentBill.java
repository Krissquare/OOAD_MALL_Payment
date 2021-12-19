package cn.edu.xmu.oomall.transaction.util;


import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentBill {


    private Long patternId;

    private String documentId;

    private Byte documentType;

    private String descr;

    private Long amount;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Payment relatedPayment;

    public String getOutTradeNo()
    {
        return TransactionPatternFactory.encodeRequestNo(relatedPayment.getId(),
                relatedPayment.getDocumentId(), relatedPayment.getDocumentType());
    }
}
