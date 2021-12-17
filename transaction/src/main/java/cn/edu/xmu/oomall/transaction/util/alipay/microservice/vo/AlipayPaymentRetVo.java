package cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo;

import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayReturnNo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlipayPaymentRetVo {
    /**
     * 商铺交易号
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * 该笔订单的资金总额，单位为人民币（分）
     */
    @JsonProperty("total_amount")
    private Long totalAmount;


    private String code;
    private String msg;

    @JsonProperty("sub_code")
    private String subCode;
    @JsonProperty("sub_msg")
    private String subMsg;


    /**
     * 固定：2088111111116894
     */
    @JsonProperty("seller_id")
    private String sellerId;
    /**
     * 固定：20161008001
     */
    @JsonProperty("merchant_order_no")
    private String merchantOrderNo;
    /**
     * 支付宝交易号固定：	2013112011001004330000121536
     */
    @JsonProperty("trade_no")
    private String tradeNo;


}
