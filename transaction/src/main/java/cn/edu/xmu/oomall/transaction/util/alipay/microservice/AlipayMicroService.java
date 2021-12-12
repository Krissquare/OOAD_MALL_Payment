package cn.edu.xmu.oomall.transaction.util.alipay.microservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/11 22:10
 */
@FeignClient(name = "alipay-service")
public interface AlipayMicroService {
    @PostMapping("internal/alipay/gateway.do")
    Object gatewayDo(@RequestParam(required = false) String app_id ,
                            @RequestParam(required = true) String method ,
                            @RequestParam(required = false) String format ,
                            @RequestParam(required = false) String charset  ,
                            @RequestParam(required = false) String sign_type  ,
                            @RequestParam(required = false) String sign  ,
                            @RequestParam(required = false) String timestamp  ,
                            @RequestParam(required = false) String notify_url   ,
                            @RequestParam(required = true) String biz_content);
}
