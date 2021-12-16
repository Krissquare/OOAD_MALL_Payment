package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AftersaleRetVo {
    private Long id;
    private String orderSn;
    private SimpleVo customer;
    private SimpleVo shop;
    private Long pid;
    private Integer state;
    private LocalDateTime confirmTime;
    private Long discountPrice;
    private Long originPrice;
    private Long point;
    private Long expressFee;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
    private Long advancesaleId;
    private Long grouponId;
    private String shipmentSn;
    private SimpleOrderitemRetVo aftersaleOrderitemVo;
}
