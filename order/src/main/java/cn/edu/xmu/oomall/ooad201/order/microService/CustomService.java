package cn.edu.xmu.oomall.ooad201.order.microService;


import cn.edu.xmu.oomall.ooad201.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "custom-service")
public interface CustomService {
    @GetMapping("/customers/{id}")
    InternalReturnObject<SimpleVo> getCustomerById(@PathVariable("id") Long id);
}
