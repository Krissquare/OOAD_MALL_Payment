package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/10 21:55
 */
@FeignClient(name = "liquidation-service")
public interface LiquidationService {
}
