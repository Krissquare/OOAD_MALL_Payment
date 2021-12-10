package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/09 19:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeChatTransactionVo {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class TransactionAmountRetVo{
        private Integer total;
        private Integer payer_total;
        private String currency;
        private String payer_currency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PayerRetVo{
        private String openid;
    }

    private String appid;

    private String mchid;

    private String out_trade_no;

    private String transaction_id;

    private String trade_type;

    private String trade_state;

    private String trade_state_desc;

    private TransactionAmountRetVo amount;

    private PayerRetVo payer;

    @JsonFormat(pattern = MyDateTime.DATE_TIME_FORMAT,timezone = "GMT+8")
    private LocalDateTime success_time;

}
