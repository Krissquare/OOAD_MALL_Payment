package cn.edu.xmu.oomall.order.model.vo;

import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/24 0:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplePayment {
    String orderSn ;
    Byte documentType;
    String descr;
    Long amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime endTime;
}
