package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRetVo {
    private Long productId;
    private Long onsaleId;
    private Long discountPrice;
    private Long activityId;
}
