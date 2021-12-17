package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice;


import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.*;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatReturnNo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/11 22:10
 */
@FeignClient(name = "wechatpay-service")
public interface WechatMicroService {

    @PostMapping("/internal/wechat/pay/transactions/jsapi")
    WechatReturnObject<WechatPaymentRetVo> requestPayment(@Validated @RequestBody WechatPaymentVo wechatPaymentVo);

    @PostMapping("/internal/wechat/refund/domestic/refunds")
    WechatReturnObject<WechatRefundRetVo> requestRefund(@Validated @RequestBody WechatRefundVo weChatPayRefundVo);

    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    WechatReturnObject<WechatPaymentQueryRetVo> queryPayment(@PathVariable(value = "out_trade_no") String  outTradeNo);

    @GetMapping("/internal/wechat/refund/domestic/refunds/{out_refund_no}")
    WechatReturnObject<WechatRefundQueryRetVo> queryRefund(@PathVariable(value = "out_refund_no") String  outTradeNo);

    @PostMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}/close")
    WechatReturnObject<WechatReturnNo> closeTransaction(@PathVariable("out_trade_no") String outTradeNo);

    @GetMapping("/internal/wechat/bill/fundflowbill")
    WechatReturnObject<WeChatPayFundFlowBillRetVo> getFundFlowBill(@RequestParam("bill_date") String billDate);
}
