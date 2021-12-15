package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorAccountUpdateVo {
    Byte state;
    String descr;
}
