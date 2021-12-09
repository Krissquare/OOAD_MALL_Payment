package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shop-service")
public interface ShopService {
    @GetMapping("/shops/{id}")
    InternalReturnObject<SimpleVo> getShopById(@PathVariable("id") Long id);
}
