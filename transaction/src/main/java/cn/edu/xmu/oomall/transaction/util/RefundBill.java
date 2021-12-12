package cn.edu.xmu.oomall.transaction.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class RefundBill {

    @NotNull
    private Long patternId;

    @NotNull
    private Long paymentId;

    @NotNull
    private String documentId;

    @NotNull
    private Byte documentType;

    @NotNull
    private String descr;

    @NotNull
    private Long amount;

    private String reason;

}
