package cn.edu.xmu.oomall.order.microservice.vo;

import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime endTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime couponTime;
    private Integer quantity;
    private Byte quantityType;
    private Byte validTerm;
    private String imageUrl;
    private String strategy;
    private Byte state;
    private SimpleVo createBy;
    private SimpleVo modifiedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime gmtModified;
}
