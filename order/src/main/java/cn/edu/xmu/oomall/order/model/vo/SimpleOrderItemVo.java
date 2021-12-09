package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 16:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderItemVo {
    @NotNull(message = "productId不能为空")
    private Long productId;
    @NotNull(message = "onsaleId不能为空")
    private Long onsaleId;
    @NotNull(message = "quantity不能为空")
    @Min(1)
    private Long quantity;
    private Long couponActId;
    private Long couponId;
}
