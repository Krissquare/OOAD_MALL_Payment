package cn.edu.xmu.oomall.order.microservice;


import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.microservice.vo.RefundRecVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundRetVo;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.oomall.order.util.MyDateTime;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/09 16:32
 */
@FeignClient(name = "transaction-service")
public interface TransactionService {
    @GetMapping("/shops/{shopId}/payment")
    ReturnObject listPayment(@PathVariable(value = "shopId") Long shopId,
                             @RequestParam(value = "documentId",required = false)String documentId,
                             @RequestParam(value = "state",required = false)Byte state,
                             @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                             @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                             @RequestParam(value="page",required = false)Integer page,
                             @RequestParam(value = "pageSize",required = false)Integer pageSize);
    @GetMapping("/shops/{shopId}/refund")
    ReturnObject listRefund(@PathVariable("shopId") Long shopId, @RequestParam(value="documentId",required = false)String documentId,
                            @RequestParam(value="state",required = false)Byte state,
                            @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                            @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @PostMapping("/internal/refunds")
    InternalReturnObject<RefundRetVo> Refund(@RequestBody RefundRecVo refundRecVo, @LoginUser Long loginUserId, @LoginName String loginUserName);
}
