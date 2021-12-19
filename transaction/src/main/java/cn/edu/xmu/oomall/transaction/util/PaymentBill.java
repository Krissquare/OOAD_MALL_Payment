package cn.edu.xmu.oomall.transaction.util;


import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endTime;

    private Payment relatedPayment;

    public String getOutTradeNo()
    {
        return TransactionPatternFactory.encodeRequestNo(relatedPayment.getId(),
                relatedPayment.getDocumentId(), relatedPayment.getDocumentType());
    }
}
