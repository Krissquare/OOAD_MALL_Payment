package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreightCalculatingRetVo {
    private Long freightPrice;
    private Long productId;
}
