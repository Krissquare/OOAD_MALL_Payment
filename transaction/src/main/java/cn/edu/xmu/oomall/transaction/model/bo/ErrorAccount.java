package cn.edu.xmu.oomall.transaction.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/18 21:22
 */
@Data
public class ErrorAccount {
    private Long id;

    private String tradeSn;

    private Long patternId;

    private Long income;

    private Long expenditure;

    private String documentId;

    private Byte state;

    private LocalDateTime time;

    private String descr;

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
