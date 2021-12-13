package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRetVo {
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
