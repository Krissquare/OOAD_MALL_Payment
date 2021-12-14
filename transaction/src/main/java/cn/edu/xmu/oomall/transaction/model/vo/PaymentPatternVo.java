package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPatternVo {
    Long id;
    String name;
    Byte state;
    LocalDateTime beginTime;
    LocalDateTime endTime;
    String className;
    SimpleVo creator;
    String gmtCreate;
    String gmtModified;
    SimpleVo modifier;
}
