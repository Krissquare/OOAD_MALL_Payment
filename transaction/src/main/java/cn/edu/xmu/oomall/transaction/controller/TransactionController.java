package cn.edu.xmu.oomall.transaction.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.model.vo.*;
import cn.edu.xmu.oomall.transaction.service.TransactionService;
import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
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

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;


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
                              @RequestParam(value = "page",required = false)Integer page,
                              @RequestParam(value = "pageSize",required = false)Integer pageSize) {
        if (shopId != 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (beginTime != null && endTime != null) {
            if (beginTime.isAfter(endTime))
                return new ReturnObject(ReturnNo.LATE_BEGINTIME);
        }
        return transactionService.listPayment(documentId, state, beginTime, endTime, page, pageSize);
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
        if (shopId != 0) {
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
        if (shopId != 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }

        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }

        return transactionService.updatePayment(id, loginUserId, loginUserName, paymentModifyVo);
    }

    /**
     * hty
     * 管理员获取退款记录
     * @param shopId
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */

    @Audit(departName="transaction")
    @GetMapping("shops/{shopId}/refund")
    public Object getRefund(@PathVariable("shopId") Long shopId, @RequestParam(value="documentId",required = false)String documentId,
                            @RequestParam(value="state",required = false)Byte state,
                            @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                            @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (beginTime != null && endTime != null && beginTime.isAfter(endTime)) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(transactionService.listRefund(documentId, state, beginTime, endTime, page, pageSize));
    }

    /**
     * hty
     * 平台管理员获取退款详情
     * @param shopId
     * @param id
     * @return
     */
    @Audit(departName = "payment")
    @GetMapping("shops/{shopId}/refund/{id}")
    public Object getRefundDetail(@PathVariable("shopId")Long shopId,@PathVariable("id")Long id) {
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(transactionService.getRefundDetail(id));
    }

    /**
     * hty
     * 平台管理员修改退款单状态
     * @param shopId
     * @param id
     * @param refundRecVo
     * @param bindingResult
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Audit(departName = "payment")
    @PutMapping("shops/{shopId}/refund/{id}")
    public Object updateRefund(@PathVariable("shopId") Long shopId, @PathVariable("id")Long id, @Validated @RequestBody RefundRecVo refundRecVo, BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName) {
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(transactionService.updateRefund(id, refundRecVo, loginUserId, loginUserName));
    }

    /**
     * gyt
     * 微信支付通知API
     * @param wechatPaymentNotifyVo
     * @return
     */
    @PostMapping("/wechat/payment/notify")
    public Object paymentNotifyByWechat(@RequestBody WechatPaymentNotifyVo wechatPaymentNotifyVo){
        return transactionService.paymentNotifyByWechat(wechatPaymentNotifyVo);
    }

    /**
     * gyt
     * 微信退款通知API
     * @param wechatRefundNotifyVo
     * @return
     */
    @PostMapping("/wechat/refund/notify")
    public Object refundNotifyByWechat(@RequestBody WechatRefundNotifyVo wechatRefundNotifyVo){
        return transactionService.refundNotifyByWechat(wechatRefundNotifyVo);
    }

    /**
     * gyt
     * 阿里异步t通知API
     * @param alipayNotifyVo
     * @return
     */
    @PostMapping("/alipay/notify")
    public Object notifyByAlipay(@RequestBody AlipayNotifyVo alipayNotifyVo){

        return transactionService.notifyByAlipay(alipayNotifyVo);
    }

    /**
     * 内部API退款
     * @param refundVo
     * @return
     */
    @PostMapping("/internal/refunds")
    public Object refund(@RequestBody RefundVo refundVo){
        return transactionService.refund(refundVo);
    }

    /**
     * 请求支付
     */
    @PostMapping("/payments")
    public Object requestPayment(@Validated @RequestBody RequestPaymentVo requestPaymentVo, BindingResult bindingResult,
                                 @LoginUser Long loginUserId, @LoginName String loginUserName) {
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }

        PaymentBill paymentBill = requestPaymentVo.createPaymentBill();
        return Common.decorateReturnObject(transactionService.requestPayment(paymentBill, loginUserId, loginUserName));
    }

}
