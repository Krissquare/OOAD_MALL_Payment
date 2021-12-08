package cn.edu.xmu.oomall.ooad201.order.model.vo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class BriefOrderVo implements Serializable {
    private Long id;
    private Long customerId;
    private Long shopId;
    private Long pid;
    private Short state;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private Long point;
    private Long grouponId;
    private Long presaleId;
    private String shipmentSn;
}
