package cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/11 22:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class AlipayRefundVo {
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    @JsonProperty("out_request_no")
    private String outRequestNo;

    @JsonProperty("refund_amount")
    private Long refundAmount;
}
