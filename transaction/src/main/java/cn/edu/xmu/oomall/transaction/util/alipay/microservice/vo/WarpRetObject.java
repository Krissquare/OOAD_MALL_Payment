package cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xucangbai
 * 最上层的包装对象
 * 当某一字段空时，不写入
 *
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WarpRetObject {
    @JsonProperty("alipay_trade_wap_pay_response")
    private AlipayPaymentRetVo alipayPaymentRetVo;

    @JsonProperty("alipay_trade_query_response")
    private AlipayPaymentQueryRetVo alipayPaymentQueryRetVo;

//    @JsonProperty("alipay_trade_close_response")
//    private CloseRetVo closeRetVo;

    @JsonProperty("alipay_trade_refund_response")
    private AlipayRefundRetVo alipayRefundRetVo;

    @JsonProperty("alipay_trade_fastpay_refund_query_response")
    private AlipayRefundQueryRetVo alipayRefundQueryRetVo;

    @JsonProperty("alipay_data_dataservice_bill_downloadurl_query_response")
    private DownloadUrlQueryRetVo downloadUrlQueryRetVo;


    private String sign;

}
