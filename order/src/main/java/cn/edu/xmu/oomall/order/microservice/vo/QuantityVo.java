package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/12 15:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityVo {
    private  Long quantity;
}
