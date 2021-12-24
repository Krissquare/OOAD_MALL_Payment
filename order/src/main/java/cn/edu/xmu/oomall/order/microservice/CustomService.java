package cn.edu.xmu.oomall.order.microservice;


import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.order.microservice.vo.CustomerModifyPointsVo;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "other-service",configuration= OpenFeignConfig.class)
public interface CustomService {
    //SOLVED BY HTY
    @GetMapping("/internal/customers/{id}")
    InternalReturnObject<SimpleVo> getCustomerById(@PathVariable("id") Long id);


    /**
     * 增加减少积点  由正负区别
     * solvedby:LXC
     * @param customerId
     * @param vo
     * @return
     */
    @PutMapping("/internal/point/{customerId}")
    InternalReturnObject<CustomerModifyPointsVo> changeCustomerPoint(@PathVariable Long customerId, @RequestBody CustomerModifyPointsVo vo);

    /**
     * 退款后，修改优惠券状态为已领取
     */
    //TODO:LXC
    @PutMapping("/internal/coupons/{id}/refund")
    InternalReturnObject refundCoupon(@RequestParam Long userId, @RequestParam String userName, @PathVariable Long id);

    /**
     * solved:LXC
     * 使用优惠券消费后，修改优惠券状态为已使用
     */
    @PutMapping("/internal/coupons/{id}/use")
    InternalReturnObject useCoupon(@RequestParam Long userId, @RequestParam String userName, @PathVariable Long id);

    /**
     * 查询优惠券是否存在
     * solved:LXC
     */
    @GetMapping("/internal/coupons/{id}/exists")
    InternalReturnObject isCouponExists(@PathVariable Long id, @RequestParam Long userId);
}
