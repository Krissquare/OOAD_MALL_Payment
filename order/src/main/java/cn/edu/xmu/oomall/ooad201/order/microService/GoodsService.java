package cn.edu.xmu.oomall.ooad201.order.microService;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.order.microService.vo.OnSaleVo;
import cn.edu.xmu.oomall.ooad201.order.microService.vo.ProductVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 16:16
 */
@FeignClient(name = "goods-service")
public interface GoodsService {
    /**
     * /internal/onsales/{id}
     * 内部API- 查询特定价格浮动的详情（2021-2-1）
     *
     * @param id
     * @return cn.edu.xmu.oomall.ooad201.order.microService.vo.OnSaleVo
     */
    @GetMapping("/internal/onsales/{id}")
    InternalReturnObject<OnSaleVo> getOnsaleById(@PathVariable Long id);

    /**
     * /products/{id}
     * 获得Product的详细信息（2021-1-1）
     *
     * @param id
     * @return cn.edu.xmu.oomall.ooad201.order.microService.vo.ProductVo
     */
    @GetMapping("/products/{id}")
    InternalReturnObject<ProductVo> getProductById(@PathVariable Long id);

}
