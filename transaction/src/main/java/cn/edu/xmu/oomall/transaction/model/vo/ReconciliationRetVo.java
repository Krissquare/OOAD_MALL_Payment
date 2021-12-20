package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/18 20:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationRetVo {
    private Integer success = 0;

    private Integer error = 0;

    private Integer extra = 0;
}
