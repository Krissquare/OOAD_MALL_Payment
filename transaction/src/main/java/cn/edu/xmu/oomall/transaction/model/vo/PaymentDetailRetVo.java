package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/09 20:07
 */
@Data
public class PaymentDetailRetVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long amount;
    private Long actualAmount;
    private String documentId;
    private Byte documentType;
    private LocalDateTime payTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Byte state;
    private String descr;
    SimpleVo adjust;
    private LocalDateTime adjustTime;
    SimpleVo creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    SimpleVo modifier;
}
