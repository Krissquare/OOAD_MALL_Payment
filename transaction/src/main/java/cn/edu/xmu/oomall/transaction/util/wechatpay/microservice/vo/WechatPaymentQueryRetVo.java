package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@Data
@NoArgsConstructor
public class WechatPaymentQueryRetVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class TransactionAmountRetVo{
        private Long total;
        private Long payerTotal;
        private String currency;
        private String payerCurrency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PayerRetVo{
        private String openid;
    }

    private String appid;

    private String mchid;

    private String outTradeNo;

    private String transactionId;

    private String tradeType;

    private String tradeState;

    private String tradeStateDesc;

    private TransactionAmountRetVo amount;

    private PayerRetVo payer;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime successTime;



}
