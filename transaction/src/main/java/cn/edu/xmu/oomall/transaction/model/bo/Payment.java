package cn.edu.xmu.oomall.transaction.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/06 22:09
 */
@Data
public class Payment {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long documentId;
    private Byte documentType;
    private String descr;
    private Long amount;
    private String actualAmount;
    private Byte state;
    private LocalDateTime payTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Long adjustId;
    private String adjustName;
    private LocalDateTime adjustTime;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
