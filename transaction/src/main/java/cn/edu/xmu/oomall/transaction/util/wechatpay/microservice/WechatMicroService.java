package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice;

import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatRequestRefundVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatRequestPaymentVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/11 22:10
 */
@FeignClient(name = "wechatpay-service")
public interface WechatMicroService {

    @PostMapping("/internal/wechat/pay/transactions/jsapi")
    InternalReturnObject requestPayment(@Validated @RequestBody WechatRequestPaymentVo wechatPaymentVo);


    @PostMapping("/internal/wechat/refund/domestic/refunds")
    InternalReturnObject requestRefund(@Validated @RequestBody WechatRequestRefundVo weChatPayRefundVo);

}
