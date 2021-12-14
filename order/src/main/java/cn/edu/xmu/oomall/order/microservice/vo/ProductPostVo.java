package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:27
 * 3-1传过去的内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPostVo {
    private Long productId;
    private Long onsaleId;
    private Long quantity;
    private Long originalPrice;
    private Long activityId;
}
