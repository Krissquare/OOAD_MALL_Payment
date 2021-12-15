package cn.edu.xmu.oomall.order.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 16:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrouponActivityVo {
    private Long id;
    private String name;
    private Long shopId;
    private List<GroupOnStrategyVo> strategy;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime endTime;
}
