package cn.edu.xmu.oomall.order.microservice;


import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "custom-service")
public interface CustomService {
    @GetMapping("/customers/{id}")
    InternalReturnObject<SimpleVo> getCustomerById(@PathVariable("id") Long id);

    //TODO: 暂未做，可能会改 //可能更改
    @GetMapping("/customers/couponId/{id}")
    InternalReturnObject getCouponById(@PathVariable("id") Long id);

    //TODO: 改状态优惠卷
    @PutMapping("/customers/couponId/{id}")
    InternalReturnObject updateCouponById(@PathVariable("id") Long id,Byte state);

    //TODO: 积点
    @PutMapping("/customers/point")
    InternalReturnObject updatePoint(@RequestParam("quantity") Long quantity);


}
