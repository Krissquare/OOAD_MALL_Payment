package cn.edu.xmu.oomall.order.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class DetailOrderVo {
    private Long id;
    private String orderSn;
    private SimpleVo customer;
    private SimpleVo shop;
    private Long pid;
    private Integer state;
    private LocalDateTime confirmTime;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private Long point;
    private String message;
    private Long regionId;
    private String address;
    private String mobile;
    private String consignee;
    private Long grouponId;
    private Long advancesaleId;
    private String shipmentSn;
    private List<SimpleOrderitemRetVo> orderItem;
}
