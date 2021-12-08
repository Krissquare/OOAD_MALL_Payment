package cn.edu.xmu.oomall.ooad201.order.microService.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 16:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrouponStrategyVo {
    private Integer quantity;
    private Integer percentage;
}
