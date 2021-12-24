package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class RefundRetVo {
    private Long id;
    private Long paymentId;
    private String tradeSn;
    private Long patternId;
    private Long amount;
    private Byte state;
    private String documentId;
    private Byte documentType;
}
