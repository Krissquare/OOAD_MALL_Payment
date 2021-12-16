package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@Data
@NoArgsConstructor
public class WechatRefundVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RefundAmountVo {
        private Long refund;
        private Long total;
        private String currency;
    }

    @NotBlank
    private String transactionId;

    @NotBlank
    private String outTradeNo;

    @NotBlank
    private String outRefundNo;

    @NotNull
    private RefundAmountVo amount;

    private String reason;


    private String notifyUrl;


}
