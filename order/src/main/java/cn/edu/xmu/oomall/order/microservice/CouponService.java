package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.order.microservice.vo.CouponActivityVo;
import cn.edu.xmu.oomall.order.microservice.vo.ProductPostVo;
import cn.edu.xmu.oomall.order.microservice.vo.ProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:26
 */
@FeignClient(name = "coupon-service",configuration= OpenFeignConfig.class)
public interface CouponService {
    //solved:LXC
    @PutMapping("/internal/discountprices")
    InternalReturnObject<List<ProductRetVo>> calculateDiscount(@RequestBody List<ProductPostVo>items);


    /**
     * cn.edu.xmu.oomall.order.microservice.vo.CouponActivityVo
     * solved:LXC
     * @param shopId
     * @param id
     * @return
     */
    @GetMapping("shops/{shopId}/couponactivities/{id}")
    InternalReturnObject<CouponActivityVo> showOwnCouponActivityInfo(@PathVariable Long shopId,
                                                                     @PathVariable Long id);
}
