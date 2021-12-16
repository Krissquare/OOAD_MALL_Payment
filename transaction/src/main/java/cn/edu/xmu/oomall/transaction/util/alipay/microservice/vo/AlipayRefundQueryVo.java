package cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class AlipayRefundQueryVo {
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    @JsonProperty("out_request_no")
    private String outRequestNo;
}
