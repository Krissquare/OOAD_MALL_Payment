package cn.edu.xmu.oomall.order.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 20:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;

    private Long orderId;

    private Long shopId;

    private Long productId;

    private Long onsaleId;

    private Long quantity;

    private Long price;

    private Long discountPrice;

    private Long point;

    private String name;

    private Long couponActivityId;

    private Long couponId;

    private Byte commented;

    private Long creatorId;

    private String creatorName;

    private Long modifierId;

    private String modifierName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
