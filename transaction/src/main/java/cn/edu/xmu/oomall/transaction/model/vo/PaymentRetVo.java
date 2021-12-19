package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/06 21:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRetVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private String documentId;
    private Byte documentType;
    private String descr;
    private Long amount;
    private String actualAmount;
    private Byte state;
    private LocalDateTime payTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
