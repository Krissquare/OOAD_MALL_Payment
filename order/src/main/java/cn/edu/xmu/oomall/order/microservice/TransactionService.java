package cn.edu.xmu.oomall.order.microservice;

import cn.edu.xmu.oomall.core.config.OpenFeignConfig;
import cn.edu.xmu.oomall.order.microservice.vo.PageVo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.microservice.vo.PaymentRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundRecVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundRetVo;
import cn.edu.xmu.oomall.order.util.MyDateTime;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/09 16:32
 */
@FeignClient(name = "transaction-service",configuration= OpenFeignConfig.class)
public interface TransactionService {
//TODO:GYT
    @GetMapping("/shops/{shopId}/payment")
    InternalReturnObject<PageVo<PaymentRetVo>> listPayment(@PathVariable(value = "shopId") Long shopId,
                                                         @RequestParam(value = "documentId",required = false)String documentId,
                                                         @RequestParam(value = "state",required = false)Byte state,
                                                         @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                                                         @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                                                         @RequestParam(value="page",required = false)Integer page,
                                                         @RequestParam(value = "pageSize",required = false)Integer pageSize);
//TODO:HTY
    @GetMapping("/shops/{shopId}/refund")
    InternalReturnObject<PageVo<RefundRetVo>> listRefund(@PathVariable("shopId") Long shopId,
                                                         @RequestParam(value="documentId",required = false)String documentId,
                                                        @RequestParam(value="state",required = false)Byte state,
                                                        @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                                                        @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                                                        @RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "pageSize", required = false) Integer pageSize);
//TODO:HTY
    @GetMapping("/internal/payment")
    InternalReturnObject<PageVo<PaymentRetVo>> listPaymentInternal(@RequestParam(value="documentId",required = false)String documentId,
                                                                  @RequestParam(value="state",required = false)Byte state,
                                                         @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                                                         @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                                                         @RequestParam(value = "page", required = false) Integer page,
                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize);
//TODO:GYT
    @PostMapping("/internal/refunds")
    InternalReturnObject<RefundRetVo> requestRefund(@Validated @RequestBody RefundRecVo refundVo);
}
