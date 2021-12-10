package cn.edu.xmu.oomall.order.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/02 17:55
 */
@Data
public class MarkShipmentVo {
    @NotNull
    String shipmentSn;
}
