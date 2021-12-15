package cn.edu.xmu.oomall.transaction.util;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentBill {

    private Long patternId;

    private String documentId;

    private Byte documentType;

    private String descr;

    private Long amount;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

}
