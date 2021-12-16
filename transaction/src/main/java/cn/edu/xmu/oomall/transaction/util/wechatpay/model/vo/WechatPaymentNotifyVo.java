package cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Data
@NoArgsConstructor
public class WechatPaymentNotifyVo {

    @Data
    @NoArgsConstructor
    public static class WeChatPayTransactionRetVo {

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public class TransactionAmountRetVo {
            private Integer total;
            private Integer payerTotal;
            private String currency;
            private String payerCurrency;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public class PayerRetVo {
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

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
        private LocalDateTime successTime;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource {
        private String algorithm;
        private String originalType;
        private WeChatPayTransactionRetVo ciphertext;
        private String nonce;
    }


    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime createTime;
    private String eventType;
    private String summary;
    private String resourceType;
    private Resource resource;

}
