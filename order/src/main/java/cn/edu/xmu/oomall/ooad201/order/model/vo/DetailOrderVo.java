package cn.edu.xmu.oomall.ooad201.order.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DetailOrderVo {
    private Long id;
    private String orderSn;
    private SimpleVo customerVo;
    private SimpleVo shopVo;
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
}
