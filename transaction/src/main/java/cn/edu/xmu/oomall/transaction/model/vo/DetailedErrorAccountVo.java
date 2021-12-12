package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedErrorAccountVo {
    Long id;
    String tradeSn;
    Long patternId;
    Long income;
    Long expenditure;
    Byte state;
    String documentId;
    String descr;
    LocalDateTime time;
    SimpleVo adjust;
    LocalDateTime adjustTime;
    SimpleVo creator;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    SimpleVo modifier;
}
