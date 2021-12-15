package cn.edu.xmu.oomall.order.microservice.vo;

import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/10 21:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityVo {
    private Long id;
    private String name;
    private SimpleVo shop;
    private LocalDateTime couponTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer quantity;
    private Byte quantityType;
    private Byte validTerm;
    private String imageUrl;
    private String strategy;
    private Byte state;
    private SimpleVo createBy;
    private SimpleVo modifiedBy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
