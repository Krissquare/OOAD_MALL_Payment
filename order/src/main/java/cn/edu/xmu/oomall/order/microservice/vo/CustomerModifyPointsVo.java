package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/14 9:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerModifyPointsVo {
    private Long points;
}
