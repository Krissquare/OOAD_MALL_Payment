package cn.edu.xmu.oomall.ooad201.order.microService.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightCalculatingPostVo {
    private Long productId;
    private Integer quantity;
    private Long freightId;
    private Integer weight;
}
