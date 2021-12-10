package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.ProductPostVo;
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
@FeignClient(name = "coupon-service")
public interface CouponService {
    @PutMapping("/internal/discountprices")
    InternalReturnObject calculateDiscoutprices(@RequestBody List<ProductPostVo> productOnsaleVos);


    @GetMapping("shops/{shopId}/couponactivities/{id}")
    InternalReturnObject showOwnCouponActivityInfo(@PathVariable Long shopId,
                                            @PathVariable Long id);
}
