package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorAccountVo {
    Long id;
    String tradeSn;
    Long patternId;
    Long income;
    Long expenditure;
    Byte state;
    String documentId;
    String time;
}
