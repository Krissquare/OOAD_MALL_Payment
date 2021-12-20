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
        private String currency = "CNY";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PayerVo{
        private String openid = "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o";
    }

    @NotBlank
    private String appid = "wxd678efh567hg6787";

    @NotBlank
    private String mchid = "1230000109";

    @NotBlank
    private String description = "Image形象店-深圳腾大-QQ公仔";

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime timeExpire = LocalDateTime.now();

    @NotBlank
    private String outTradeNo;

    @NotNull
    private TransactionAmountVo amount = new TransactionAmountVo();

    @NotNull
    private PayerVo payer = new PayerVo();

    @NotBlank
    private String notifyUrl = "https://www.weixin.qq.com/wxpay/pay.php";

}