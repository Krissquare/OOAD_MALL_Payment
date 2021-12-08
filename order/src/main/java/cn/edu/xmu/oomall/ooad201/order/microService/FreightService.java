package cn.edu.xmu.oomall.ooad201.order.microService;

import cn.edu.xmu.oomall.ooad201.order.microService.vo.FreightCalculatingPostVo;
import cn.edu.xmu.oomall.ooad201.order.microService.vo.FreightCalculatingRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:12
 */
@FeignClient(name = "freight-service")
public interface FreightService {
    /**
     * regions/{rid}/price（2021-2-2）
     * 内部API-计算一批商品的运费
     *
     * @param rid   地区id
     * @param items
     * @return cn.edu.xmu.oomall.ooad201.order.microService.vo.FreightCalculatingRetVo
     */
    @PostMapping("/regions/{rid}/price")
    InternalReturnObject<FreightCalculatingRetVo> calculateFreight(@PathVariable Long rid, @RequestBody List<FreightCalculatingPostVo> items);
}
