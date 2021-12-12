package cn.edu.xmu.oomall.order.microservice.vo;

import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 16:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvanceVo {
    private Long id;
    private String name;
    private SimpleVo shop;
    private ProductSimpleVo product;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime payTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime endTime;
    private Long price;
    private Long quantity;
    private Long advancePayPrice;
}
