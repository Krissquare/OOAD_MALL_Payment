package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/09 19:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatPaymentNotifyVo {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Resource{
        private String algorithm;
        private String original_type;
        private WeChatTransactionVo ciphertext;
        private String nonce;
    }

    private String id;
    @JsonFormat(pattern = MyDateTime.DATE_TIME_FORMAT,timezone = "GMT+8")
    private LocalDateTime create_time;
    private String event_type;
    private String summary;
    private String resource_type;
    private Resource resource;

}
