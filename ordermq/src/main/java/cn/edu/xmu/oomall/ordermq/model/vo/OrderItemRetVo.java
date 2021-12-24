package cn.edu.xmu.oomall.ordermq.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * modified by gyt at 2021/12/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRetVo {
    private Long id;
    private Long orderId;
    private Long shopId;
    private Long productId;
    private Long onsaleId;
    private String name;
    private Long quantity;
    private Long price;
    private Long discountPrice;
    private Long point;
    private Long couponId;
    private Long couponActivityId;
    private Long customerId;
}
