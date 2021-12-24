package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundVo {

    private String documentId;

    private Byte documentType;

    private Long paymentId;

    private String descr;

    private Long amount;

    private String reason;
}
