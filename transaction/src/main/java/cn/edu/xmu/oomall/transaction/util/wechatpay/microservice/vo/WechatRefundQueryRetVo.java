package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@Data
@NoArgsConstructor
public class WechatRefundQueryRetVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class RefundAmountRetVo{
        private Integer total;
        private Integer refund;
        private Integer payerTotal;
        private Integer payerRefund;
        private Integer settlementRefund;
        private Integer settlementTotal;
        private Integer discountRefund;
        private String currency;
    }

    private String refundId;

    private String outRefundNo;

    private String transactionId;

    private String outTradeNo;

    private String channel;

    private String userReceivedAccount;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime successTime;

    private String status;

    private RefundAmountRetVo amount;


}
