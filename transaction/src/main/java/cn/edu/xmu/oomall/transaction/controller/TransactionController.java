package cn.edu.xmu.oomall.transaction.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.model.vo.PaymentModifyVo;
import cn.edu.xmu.oomall.transaction.service.TransactionService;
import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

import static cn.edu.xmu.oomall.core.util.Common.processFieldErrors;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.transaction.model.vo.RefundRecVo;


@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @Autowired
    private HttpServletResponse httpServletResponse;
    /**
     * gyt
     * 平台管理员查询支付信息
     * @param shopId
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/payment")
    public Object listPayment(@PathVariable(value = "shopId") Long shopId,
                              @RequestParam(value = "documentId",required = false)String documentId,
                              @RequestParam(value = "state",required = false)Byte state,
                              @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                              @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                              @RequestParam(value="page",required = false)Integer page,
                              @RequestParam(value = "pageSize",required = false)Integer pageSize) {
        if(shopId!=0)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if(beginTime!=null&&endTime!=null)
        {
            if(beginTime.isAfter(endTime))
                return new ReturnObject(ReturnNo.LATE_BEGINTIME);
        }
        return transactionService.listPayment(documentId,state,beginTime,endTime,page,pageSize);
    }

    /**
     * gyt
     * 平台管理员查询支付信息详情
     * @param shopId
     * @param id
     * @return
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/payment/{id}")
    public Object getPaymentDetails(@PathVariable(value = "shopId") Long shopId,
                                    @PathVariable(value = "id")Long id) {
        if(shopId!=0)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        return transactionService.getPaymentDetails(id);
    }

    /**
     * gyt
     * 平台管理员修改支付信息
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @param paymentModifyVo
     * @param bindingResult
     * @return
     */
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/payment/{id}")
    public Object updatePayment(@LoginUser Long loginUserId,
                                @LoginName String loginUserName,
                                @PathVariable(value = "shopId") Long shopId,
                                @PathVariable(value = "id")Long id,
                                @Validated @RequestBody PaymentModifyVo paymentModifyVo,
                                BindingResult bindingResult) {
        if(shopId!=0)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        Object object = processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        return transactionService.updatePayment(id,loginUserId,loginUserName, paymentModifyVo);
    }


    @Audit(departName="transaction")
    @GetMapping("shops/{shopId}/refund")
    public Object getRefund(@PathVariable("shopId") Long shopId, @RequestParam(value="documentId",required = false)String documentId,
                            @RequestParam(value="state",required = false)Byte state,
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime beginTime,
                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime endTime,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        if(beginTime!=null&&endTime!=null&&beginTime.isAfter(endTime))
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        if(shopId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(transactionService.listRefund(documentId,state,beginTime,endTime,page, pageSize));
    }
    @Audit(departName = "payment")
    @GetMapping("shops/{shopId}/refund/{id}")
    public Object getRefundDetail(@PathVariable("shopId")Long shopId,@PathVariable("id")Long id)
    {
        if(shopId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(transactionService.getRefundDetail(id));
    }

    @Audit(departName = "payment")
    @PutMapping("shops/{shopId}/refund/{id}")
    public Object updateRefund(@PathVariable("shopId") Long shopId, @PathVariable("id")Long id, @Validated @RequestBody RefundRecVo refundRecVo, BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        if(shopId!=0)
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(transactionService.updateRefund(id,refundRecVo,loginUserId,loginUserName));
    }

}
