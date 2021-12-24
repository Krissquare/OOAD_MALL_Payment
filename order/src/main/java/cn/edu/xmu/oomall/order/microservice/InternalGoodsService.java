package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.order.microservice.vo.IntegerQuantityVo;
import cn.edu.xmu.oomall.order.microservice.vo.QuantityVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/24 15:17
 */
@FeignClient(name = "goods-service")
public interface InternalGoodsService {
    @PutMapping("internal/onsales/{id}/stock")
    InternalReturnObject updateOnsaleQuantity(@PathVariable Long id, @RequestBody IntegerQuantityVo vo);

}
