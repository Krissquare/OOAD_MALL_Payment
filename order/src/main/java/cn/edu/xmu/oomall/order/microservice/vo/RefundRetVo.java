package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRetVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long amount;
    private Byte state;
    private String documentId;
    private Byte documentType;
}
