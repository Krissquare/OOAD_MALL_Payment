package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    public class Amount{
        private Integer total;
        private Integer refund;
        private Integer payer_total;
        private Integer payer_refund;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ciphertext{
        private String mchid;
        private String outTrade_no;
        private String transaction_id;
        private String out_refund_no;
        private String refund_id;
        private String refund_status;
        private String user_received_account;
        private Amount amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource{
        private String algorithm;
        private String original_type;
        private Ciphertext ciphertext;
        private String nonce;
    }

    private String id;
    @JsonFormat(pattern = MyDateTime.DATE_TIME_FORMAT,timezone = "GMT+8")
    private LocalDateTime create_time;
    private String event_type;
    private String resource_type;
    private Resource resource;

}
