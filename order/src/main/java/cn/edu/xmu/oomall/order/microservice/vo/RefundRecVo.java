package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRecVo {
    private Long patternId;
    private String documentId;
    private Long paymentId;
    private String descr;
    private Long amount;
    private Byte documentType;
}
