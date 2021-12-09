package cn.edu.xmu.oomall.ooad201.payment.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.payment.model.vo.RefundRecVo;
import cn.edu.xmu.oomall.ooad201.payment.service.PaymentService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @Audit(departName="payment")
    @GetMapping("shops/{shopId}/refund")
    public Object getRefund(@PathVariable("shopId") Long shopId,@RequestParam(value="documentId",required = false)String documentId,
            @RequestParam(value="state",required = false)Byte state,
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime beginTime,
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime endTime,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        if(shopId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(paymentService.getRefund(documentId,state,beginTime,endTime,page, pageSize));
    }
    @Audit(departName = "payment")
    @GetMapping("shops/{shopId}/refund/{id}")
    public Object getRefundDetail(@PathVariable("shopId")Long shopId,@PathVariable("id")Long id)
    {
        if(shopId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(paymentService.getRefundDetail(id));
    }

    @Audit(departName = "payment")
    @PutMapping("shops/{shopId}/refund/{id}")
    public Object updateRefund(@PathVariable("shopId") Long shopId, @PathVariable("id")Long id, @Validated @RequestBody RefundRecVo refundRecVo, BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "传入的RequestBody参数格式不合法"));
        }
        if(shopId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(paymentService.updateRefund(id,refundRecVo,loginUserId,loginUserName));
    }

}
