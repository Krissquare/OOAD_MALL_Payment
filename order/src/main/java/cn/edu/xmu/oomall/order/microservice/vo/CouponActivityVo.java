package cn.edu.xmu.oomall.order.microservice.vo;

import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Length(max = 100,message = "name字符串最大长度为100")
    private String name;
    @Min(value = 0,message = "quantity最小值为0")
    private Integer quantity;
    @Min(value = 0,message = "quantityType最小值为0")
    @Max(value = 1,message = "quantityType最大值为1")
    private Byte quantityType;
    @Min(value = 0,message = "validTerm最小值为0")
    @Max(value = 1,message = "validTerm最大值为1")
    private Byte validTerm;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime couponTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime endTime;

    private String strategy;

    private Integer numKey;
}
