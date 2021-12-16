package cn.edu.xmu.oomall.transaction.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefundBill {

    private Long patternId;

    private Long paymentId;

    private String documentId;

    private Byte documentType;

    private String descr;

    private Long amount;

    private Long total;

    private String reason;

}
