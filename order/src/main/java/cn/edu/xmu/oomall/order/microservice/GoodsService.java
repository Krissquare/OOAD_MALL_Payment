package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.order.microservice.vo.OnSaleVo;
import cn.edu.xmu.oomall.order.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.order.microservice.vo.QuantityVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 16:16
 */
@FeignClient(name = "goods-service",configuration= OpenFeignConfig.class)
public interface GoodsService {
    /**
     * /internal/onsales/{id}
     * 内部API- 查询特定价格浮动的详情（2021-2-1）
     *
     * @param id
     * @return cn.edu.xmu.oomall.ooad201.order.microService.vo.OnSaleVo
     */
    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<OnSaleVo> selectFullOnsale(@PathVariable("id")Long id);

    /**
     * /products/{id}
     * 获得Product的详细信息（2021-1-1）
     *
     * @param id
     * @return cn.edu.xmu.oomall.ooad201.order.microService.vo.ProductVo
     */
    @GetMapping("/products/{id}")
    InternalReturnObject<ProductVo> getProductDetails(@PathVariable Long id) ;


    /**
     * internal/shops/{did}/onsales/{id}/decr
     * 内部API-扣减库存数量（2021-3-2）
     * @param did
     * @param id
     * @param vo
     * @return
     */
    @PutMapping("internal/shops/{did}/onsales/{id}/decr")
    InternalReturnObject decreaseOnSale(@PathVariable Long did, @PathVariable Long id,@RequestBody QuantityVo vo);
}
