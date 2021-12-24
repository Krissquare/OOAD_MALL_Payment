package cn.edu.xmu.oomall.ordermq.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 9:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private Long id;

    private Long customerId;

    private Long shopId;

    private String orderSn;

    private Long pid;

    private String consignee;

    private Long regionId;

    private String address;

    private String mobile;

    private String message;

    private Long advancesaleId;

    private Long grouponId;

    private Long expressFee;

    private Long discountPrice;

    private Long originPrice;

    private Long point;

    private LocalDateTime confirmTime;

    private String shipmentSn;

    private Integer state;

    private Byte beDeleted;

    private Long creatorId;

    private String creatorName;

    private Long modifierId;

    private String modifierName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
