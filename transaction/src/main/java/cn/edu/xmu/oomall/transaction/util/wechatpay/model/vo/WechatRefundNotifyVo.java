package cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRules;
import java.util.zip.ZipEntry;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/09 19:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatRefundNotifyVo {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount{
        private Integer total;
        private Integer refund;
        private Integer payerTotal;
        private Integer payerRefund;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ciphertext{
        private String mchid;
        private String outTradeNo;
        private String transactionId;
        private String outRefundNo;
        private String refundId;
        private String refundStatus;
        private String userReceivedAccount;
        private Amount amount;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
        private LocalDateTime successTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource{
        private String algorithm;
        private String originalType;
        private Ciphertext ciphertext;
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
