package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/02 17:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkShipmentVo {
    @NotNull
    String shipmentSn;
}
