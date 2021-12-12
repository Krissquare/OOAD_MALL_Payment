package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice;

import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatpayPaymentVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wechatpay-service")
public interface WechatpayService {

    @PostMapping("/internal/wechat/pay/transactions/jsapi")
    InternalReturnObject<> getAdvanceSaleById(@Validated @RequestBody WechatpayPaymentVo weChatPayTransactionVo);

}
