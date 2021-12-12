package cn.edu.xmu.oomall.transaction.util.wechatpay.microservice;

import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WeChatRefundVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/11 22:10
 */
@FeignClient(name = "wechatpay-service")
public interface WeChatMicroService {
    @PostMapping("/internal/wechat/refund/domestic/refunds")
    Object createRefund(@Validated @RequestBody WeChatRefundVo weChatPayRefundVo);

}
