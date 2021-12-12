package cn.edu.xmu.oomall.transaction.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentBill {
    @NotNull
    private Long patternId;

    @NotNull
    private String documentId;

    @NotNull
    private Byte documentType;

    @NotNull
    private String descr;

    @NotNull
    private Long amount;

    @NotNull
    private LocalDateTime beginTime;

    @NotNull
    private LocalDateTime endTime;
}
