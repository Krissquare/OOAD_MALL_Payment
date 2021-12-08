package cn.edu.xmu.oomall.ooad201.order.microService.vo;

import cn.edu.xmu.oomall.ooad201.order.model.vo.SimpleVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 16:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnSaleVo {
    private Long id;
    private Long price;
    private Integer quantity;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime endTime;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Integer numKey;
    private Integer maxQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime gmtCreate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime gmtModified;
    private ProductSimpleVo product;
    private SimpleVo shop;
    private SimpleVo creator;
    private SimpleVo modifier;

}
