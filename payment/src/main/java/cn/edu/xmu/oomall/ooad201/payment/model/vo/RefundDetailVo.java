package cn.edu.xmu.oomall.ooad201.payment.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RefundDetailVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long paymentId;
    private Long amount;
    private Byte state;
    private String documentId;
    private Byte documentType;
    private String descr;
    private Long adjustId;
    private String adjustName;
    private LocalDateTime adjustTime;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long modifierId;
    private String modifierName;
}
