package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WechatPaymentVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class TransactionAmountVo{
        // 由Integer修改为Long
        private Long total;
        private String currency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PayerVo{
        private String openid;
    }

    @NotBlank
    private String appid;

    @NotBlank
    private String mchid;

    @NotBlank
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime timeExpire;

    @NotBlank
    private String outTradeNo;

    @NotNull
    private TransactionAmountVo amount = new TransactionAmountVo();

    @NotNull
    private PayerVo payer = new PayerVo();

    @NotBlank
    private String notifyUrl;

}