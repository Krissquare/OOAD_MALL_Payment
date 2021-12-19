package cn.edu.xmu.oomall.transaction.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/06 22:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private String documentId;
    private Byte documentType;
    private String descr;
    private Long amount;
    private Long actualAmount;
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
