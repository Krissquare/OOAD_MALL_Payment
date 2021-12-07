package cn.edu.xmu.oomall.ooad201.order.microService.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPostVo {
    private Long productId;
    private Long onsaleId;
    private Integer quantity;
    private Long originalPrice;
    private Long activityId;
    private Long categoryId;
}
