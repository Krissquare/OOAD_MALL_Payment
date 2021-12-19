package cn.edu.xmu.oomall.transaction.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.model.vo.*;
import cn.edu.xmu.oomall.transaction.service.TransactionService;
import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


import cn.edu.xmu.oomall.core.util.Common;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    /**
     * fz
     * 1.获得支付渠道的所有状态
     * */
    @GetMapping("/paypatterns/states")
    public Object listAllPayPatternStates(){
        //TODO: qm没给出支付渠道状态图，待补
        return new ReturnObject<>();
    }
    /**
     * @author fz
     * 2.获取当前有效的支付渠道
     * */
    @GetMapping("/paypatterns")
    @Audit(departName = "payment")
    public Object listAllValidPayPatterns(@LoginUser Long userId){
        return Common.decorateReturnObject(transactionService.listAllValidPayPatterns());
    }
    /**
     * fz
     * 3.获得所有的支付渠道
     * */
    @GetMapping("/shops/{shopId}/paypatterns")
    @Audit(departName = "payment")
    public Object listAllPayPatterns(@PathVariable("shopId") Long shopId){
        if (shopId != 0){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        return Common.decorateReturnObject(transactionService.listAllPayPatterns());
    }
    /**
     * @author fz
     * 4.获得所有支付单状态
     * */
    @GetMapping("/payments/states")
    public Object listAllPaymentStates(){
        return Common.decorateReturnObject(transactionService.listAllPaymentStates());
    }

    /**
     * @author fz
     * 顾客支付已建立的支付单
     * */
    @PutMapping("/payments/{pid}/pay")
    public Object paymentPayedByCustomer(@LoginUser Long userId,
                                         @PathVariable("pid") Long pid,
                                         @Validated @RequestBody PaymentBePayedVo payedVo, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        return Common.decorateReturnObject(transactionService.paymentPayedByCustomer(pid,userId,payedVo));
    }

    /**
     * 6.顾客请求支付
     * hqg
     */
    @PostMapping("/payments")
    @Audit(departName = "payment")
    public Object requestPayment(@Validated @RequestBody RequestPaymentVo requestPaymentVo, BindingResult bindingResult,
                                 @LoginUser Long loginUserId, @LoginName String loginUserName) {
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }

        PaymentBill paymentBill = requestPaymentVo.createPaymentBill();
        return Common.decorateReturnObject(transactionService.requestPayment(paymentBill, loginUserId, loginUserName));
    }
    /**
     * gyt
     * 7.平台管理员查询支付信息
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
                              @RequestParam(value = "beginTime",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginTime,
                              @RequestParam(value = "endTime",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime,
                              @RequestParam(value = "page",required = false)Integer page,
                              @RequestParam(value = "pageSize",required = false)Integer pageSize) {
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        if (beginTime != null && endTime != null) {
            if (beginTime.isAfter(endTime))
            {
                return new ReturnObject(ReturnNo.LATE_BEGINTIME);
            }
        }
        return transactionService.listPayment(documentId, state, beginTime.toLocalDateTime(), endTime.toLocalDateTime(), page, pageSize);
    }

    /**
     * 内部API，只给售后用
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/internal/payment")
    public Object listPaymentInternal(@RequestParam(value = "documentId",required = false)String documentId,
                              @RequestParam(value = "state",required = false)Byte state,
                              @RequestParam(value = "beginTime",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginTime,
                              @RequestParam(value = "endTime",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime,
                              @RequestParam(value = "page",required = false)Integer page,
                              @RequestParam(value = "pageSize",required = false)Integer pageSize) {
        if (beginTime != null && endTime != null) {
            if (beginTime.isAfter(endTime))
            {
                return new ReturnObject(ReturnNo.LATE_BEGINTIME);
            }
        }
        return transactionService.listPayment(documentId, state, beginTime.toLocalDateTime(), endTime.toLocalDateTime(), page, pageSize);
    }

    /**
     * gyt
     * 8.平台管理员查询支付信息详情
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
     * 9.平台管理员修改支付信息
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
     * 10.管理员获取退款记录
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
    @GetMapping("/shops/{shopId}/refund")
    public Object getRefund(@PathVariable("shopId") Long shopId, @RequestParam(value="documentId",required = false)String documentId,
                            @RequestParam(value="state",required = false)Byte state,
                            @RequestParam(value = "beginTime",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginTime,
                            @RequestParam(value = "endTime",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (beginTime != null && endTime != null && beginTime.isAfter(endTime)) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        final ReturnObject refund = transactionService.getRefund(documentId, state, beginTime.toLocalDateTime(), endTime.toLocalDateTime(), page, pageSize);
        System.out.println(refund);
        return Common.decorateReturnObject(refund);
    }

    /**
     * hty
     * 11.平台管理员获取退款详情
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
     * 12.平台管理员修改退款单状态
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
     * b-3 fz
     * 13.平台管理员查询错账信息
     * */
    @GetMapping("/shops/{shopId}/erroraccounts")
    @Audit(departName = "payment")
    public Object listAllErrorAccountsByAdmin(@PathVariable("shopId") Long shopId,
                                              @LoginUser Long adminId,
                                              @RequestParam(value = "documentId", required = false) String documentId,
                                              @RequestParam(value = "state", required = false) Byte state,
                                              @RequestParam(value = "beginTime", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginTime,
                                              @RequestParam(value = "endTime", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endTime,
                                              @RequestParam(value = "page", required = false) Integer page,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize){
        if (endTime == null || beginTime == null){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        if (shopId != 0){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        if (beginTime.isAfter(endTime)){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        return Common.decorateReturnObject(transactionService.listErrorAccountsByConditions(documentId,state,beginTime.toLocalDateTime(),endTime.toLocalDateTime(),page,pageSize));
    }

    /**
     * b-3 fz
     * 14.平台管理员查询错账信息详情
     * */
    @GetMapping("/shops/{shopId}/erroraccounts/{id}")
    @Audit(departName = "payment")
    public Object getDetailedErrorAccountByAdmin(@PathVariable("shopId") Long shopId,
                                                 @PathVariable("id") Long id){
        if (shopId!=0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        return Common.decorateReturnObject(transactionService.getDetailedErrorAccount(id));
    }

    /**
     * b-3 fz
     * 15.平台管理员修改错账信息
     * */
    @PostMapping("/shops/{shopId}/erroraccounts/{id}")
    @Audit(departName = "payment")
    public Object updateErrorAccountByAdmin(@PathVariable("shopId") Long shopId,
                                            @PathVariable("id") Long id,
                                            @LoginUser Long adminId,
                                            @LoginUser String adminName,
                                            @RequestBody ErrorAccountUpdateVo updateVo){
        return Common.decorateReturnObject(transactionService.updateErrorAccount(adminId, adminName, id, updateVo));
    }
    @GetMapping("/shops/{id}/reconciliation")
    public Object reconciliation(@PathVariable("id") Long id,
                                 @RequestParam(value = "beginTime")LocalDateTime beginTime,
                                 @RequestParam(value = "endTime")LocalDateTime endTime){
        return Common.decorateReturnObject(transactionService.reconciliation(beginTime,endTime));
    }

    /**
     * 内部API退款
     * @param refundVo
     * @return
     */
    @PostMapping("/internal/refunds")
    public Object requestRefund(@RequestBody RefundVo refundVo){
        RefundBill refundBill = refundVo.createRefundBill();
        return Common.decorateReturnObject(transactionService.requestRefund(refundBill));
    }
}
