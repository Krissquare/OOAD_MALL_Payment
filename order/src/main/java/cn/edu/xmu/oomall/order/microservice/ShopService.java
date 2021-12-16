package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shop-service",configuration= OpenFeignConfig.class)
public interface ShopService {
    //SOLVED BY HTY
    @GetMapping("/shops/{id}")
    InternalReturnObject<SimpleVo> getSimpleShopById(@PathVariable Long id);
    //getShopById(@PathVariable("id") Long id);
}
