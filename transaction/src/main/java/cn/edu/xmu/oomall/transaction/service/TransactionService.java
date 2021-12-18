package cn.edu.xmu.oomall.transaction.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentState;
import cn.edu.xmu.oomall.transaction.model.po.ErrorAccountPo;
import cn.edu.xmu.oomall.transaction.model.po.PaymentPatternPo;
import cn.edu.xmu.oomall.transaction.model.vo.*;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;

@Service
public class TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;
    /**
     * 2.获取当前有效的支付渠道
     * fz
     * */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listAllValidPayPatterns(){
        ReturnObject ret = transactionDao.listAllPayPattern();
        List<PaymentPatternPo> oriList = (List<PaymentPatternPo>) ret.getData();
        List<SimpleVo> retList = new ArrayList<>();
        for (PaymentPatternPo item: oriList){
            if (item.getState() == null) {
                SimpleVo simpleVo = cloneVo(item, SimpleVo.class);
                retList.add(simpleVo);
            }
        }
        return new ReturnObject(retList);
    }
    /**
     * 3.获得所有的支付渠道
     * fz
     * */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listAllPayPatterns(){
        ReturnObject ret = transactionDao.listAllPayPattern();
        List<PaymentPatternPo> oriList = (List<PaymentPatternPo>) ret.getData();
        List<PaymentPatternVo> tarList = new ArrayList<>();
        for (PaymentPatternPo item: oriList){
            SimpleVo creator = new SimpleVo();
            creator.setId(item.getCreatorId());
            creator.setName(item.getCreatorName());
            SimpleVo modifier = new SimpleVo();
            modifier.setId(item.getModifierId());
            modifier.setName(item.getModifierName());
            PaymentPatternVo tarItem = cloneVo(item, PaymentPatternVo.class);
            tarItem.setCreator(creator);
            tarItem.setModifier(modifier);
            tarList.add(tarItem);
        }
        return new ReturnObject(tarList);
    }
    /**
     * 4.获得所有支付单状态
     * fz
     * */
    public ReturnObject listAllPaymentStates(){
        HashMap<Byte, String> states = new HashMap<>();
        for (PaymentState item: PaymentState.values()){
            states.put(item.getCode(),item.getState());
        }
        return new ReturnObject(states);
    }
    /**
     * 6.顾客请求支付
     * hqg
     * @param paymentBill
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject requestPayment(PaymentBill paymentBill, Long loginUserId, String loginUserName) {
        // 根据documentId, documentType去查payment
        ReturnObject<PageInfo<Payment>> retPaymentPageInfo =
                transactionDao.listPayment(null, paymentBill.getDocumentId(), paymentBill.getDocumentType(), null, null, null, 1, 100);
        if (!retPaymentPageInfo.getCode().equals(ReturnNo.OK)) {
            return retPaymentPageInfo;
        }

        // 获取paymentList
        Map<String, Object> retMap = (Map<String, Object>) retPaymentPageInfo.getData();
        List<Payment> paymentList = (List<Payment>) retMap.get("list");

        Payment validExistedPayment = null;
        for (Payment payment : paymentList) {
            // 判断是否存在已支付、已对账、已清算的支付流水
            if (payment.getState().equals(PaymentState.ALREADY_PAY.getCode()) ||
                    payment.getState().equals(PaymentState.ALREADY_RECONCILIATION.getCode()) ||
                    payment.getState().equals(PaymentState.ALREADY_LIQUIDATION.getCode())) {
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }

            // TODO: 判断是否在beginTime和endTime内

            // 判断是否存在匹配支付渠道的待支付流水
            if (paymentBill.getPatternId().equals(payment.getPatternId()) &&
                    payment.getState().equals(PaymentState.WAIT_PAY.getCode())) {
                validExistedPayment = payment;
            }
        }

        // 开始请求支付
        TransactionPattern pattern = transactionPatternFactory.getPatternInstance(paymentBill.getPatternId());

        // 不存在匹配的流水，则需要新建
        if (validExistedPayment == null) {
            Payment payment = cloneVo(paymentBill, Payment.class);
            // TODO: userId和userName
            setPoCreatedFields(payment, 1L, "hqg");
            setPoModifiedFields(payment, 1L, "hqg");
            ReturnObject<Payment> retPayment = transactionDao.insertPayment(payment);
            if (!retPayment.getCode().equals(ReturnNo.OK)) {
                return retPayment;
            }
            validExistedPayment = retPayment.getData();
        }

        // 然后请求支付
        // 创建请求号
        String requestNo = transactionPatternFactory.encodeRequestNo(validExistedPayment.getId(),
                validExistedPayment.getDocumentId(), validExistedPayment.getDocumentType());
        pattern.requestPayment(requestNo, paymentBill);

        return new ReturnObject<>(ReturnNo.OK);
    }
    /**
     * gyt
     * 7.平台管理员查询支付信息
     *
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listPayment(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        return transactionDao.listPayment(null, documentId, null, state, beginTime, endTime, page, pageSize);
    }

    /**
     * gyt
     * 8.平台管理员查询支付信息详情
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentDetails(Long id) {
        ReturnObject returnObject = transactionDao.getPaymentById(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        PaymentDetailRetVo paymentDetailRetVo = cloneVo(returnObject.getData(), PaymentDetailRetVo.class);
        return new ReturnObject(paymentDetailRetVo);
    }

    /**
     * gyt
     * 9.平台管理员修改支付信息
     *
     * @param id
     * @param loginUserId
     * @param loginUserName
     * @param paymentModifyVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updatePayment(Long id, Long loginUserId, String loginUserName, PaymentModifyVo paymentModifyVo) {
        ReturnObject returnObject = transactionDao.getPaymentById(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        Payment payment = (Payment) returnObject.getData();
        if (payment.getState().equals(PaymentState.ALREADY_PAY.getCode()) || payment.getState().equals(PaymentState.FAIL.getCode())) {
            payment.setState(paymentModifyVo.getState());
            payment.setDescr(paymentModifyVo.getDescr());
            setPoModifiedFields(payment, loginUserId, loginUserName);
            ReturnObject returnObject1 = transactionDao.updatePayment(payment);
            if (!returnObject1.getCode().equals(ReturnNo.OK)) {
                return returnObject1;
            }
            PaymentDetailRetVo paymentDetailRetVo = cloneVo(payment, PaymentDetailRetVo.class);
            return new ReturnObject(paymentDetailRetVo);
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }

    /**
     * hty
     * 10.平台管理员查询退款
     *
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getRefund(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        return transactionDao.listRefund(null, documentId, state, null, null, beginTime, endTime, page, pageSize);
    }

    /**
     * hty
     * 11.获取退款详情
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getRefundDetail(Long id) {
        ReturnObject ret = transactionDao.getRefundById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        RefundDetailVo returnDetailVo = cloneVo(ret.getData(), RefundDetailVo.class);
        return new ReturnObject(returnDetailVo);
    }

    /**
     * hty
     * 12.修改退款信息
     *
     * @param id
     * @param refundRecVo
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateRefund(Long id, RefundRecVo refundRecVo, Long loginUserId, String loginUserName) {
        ReturnObject ret = transactionDao.getRefundById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Refund refund1 = (Refund) ret.getData();
//        if (!refund1.getState().equals(RefundState.FINISH_REFUND)) {
//            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
//        }
        refund1.setState(refundRecVo.getState());
        refund1.setDescr(refundRecVo.getDescr());
        setPoModifiedFields(refund1, loginUserId, loginUserName);
        refund1.setAdjustId(loginUserId);
        refund1.setAdjustName(loginUserName);
        ReturnObject returnObject = transactionDao.updateRefund(refund1);
        if (!returnObject.getCode().equals(ReturnNo.OK.getCode())) {
            return returnObject;
        }
        return new ReturnObject(cloneVo(refund1, RefundDetailVo.class));
    }

    /**
     * 13.平台管理员查询错账信息
     * fz
     * */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listErrorAccountsByConditions(String documentId,
                                                      Byte state,
                                                      LocalDateTime beginTime,
                                                      LocalDateTime endTime,
                                                      Integer page,
                                                      Integer pageSize){
        return transactionDao.listErrorAccountsVoByConditions(documentId,state,beginTime,endTime,page,pageSize);
    }

    /**
     * 14.平台管理员查询错账信息详情
     * fz
     * */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getDetailedErrorAccount(Long id){
        ReturnObject ret = transactionDao.getErrorAccount(id);
        if (!ret.getCode().equals(ReturnNo.OK)){
            return ret;
        }
        ErrorAccountPo ori = (ErrorAccountPo) ret.getData();
        ErrorAccountDetailedVo tar = ErrorAccountDetailedVo.generateFromErrorAccountPo(ori);
        return new ReturnObject(tar);
    }

    /**
     * 15.平台管理员修改错账信息
     * fz
     * */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateErrorAccount(Long id, ErrorAccountUpdateVo updateVo){
        ReturnObject ret = transactionDao.getErrorAccount(id);
        if (!ret.getCode().equals(ReturnNo.OK)){
            return ret;
        }
        ErrorAccountPo po = (ErrorAccountPo) ret.getData();
        if (po.getState() != 0){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        po.setDescr(updateVo.getDescr());
        po.setState(updateVo.getState());
        ReturnObject updRet = transactionDao.updateErrorAccount(po);
        if (!updRet.getCode().equals(ReturnNo.OK)){
            return ret;
        }
        ErrorAccountDetailedVo retData = ErrorAccountDetailedVo.generateFromErrorAccountPo(po);
        return new ReturnObject(retData);
    }


    /**
     * 内部API退款
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject requestRefund(RefundBill refundBill) {
        // 查payment是否存在
        ReturnObject<Payment> retPayment = transactionDao.getPaymentById(refundBill.getPaymentId());
        if (retPayment.getData() == null) {
            return retPayment;
        }

        // 判断amount是否超出payment的支付金额
        if (refundBill.getAmount() > retPayment.getData().getActualAmount()) {
            return new ReturnObject<>(ReturnNo.REFUND_MORE);
        }
        refundBill.setTotal(retPayment.getData().getActualAmount());

        // 根据documentId和paymentId去查refund
        ReturnObject<PageInfo<Payment>> retRefundPageInfo =
                transactionDao.listRefund(refundBill.getPaymentId(), refundBill.getDocumentId(), null, null, null, null, null, 1, 100);
        if (!retRefundPageInfo.getCode().equals(ReturnNo.OK)) {
            return retRefundPageInfo;
        }

        // 获取refundList
        Map<String, Object> retMap = (Map<String, Object>) retRefundPageInfo.getData();
        List<Refund> refundList = (List<Refund>) retMap.get("list");

        // 同一时刻同一个documentId和paymentId（一个单子）至多只允许存在一笔待退款的流水
        for (Refund refund : refundList) {
            // 需要判断，避免重复退款
            // 判断是否待退款、已退款、已对账、已清算
            if (refund.getState().equals(RefundState.WAIT_REFUND.getCode()) ||
                    refund.getState().equals(RefundState.FINISH_RECONCILIATION.getCode()) ||
                    refund.getState().equals(RefundState.FINISH_LIQUIDATION.getCode()) ||
                    refund.getState().equals(RefundState.FINISH_REFUND.getCode())) {
                return new ReturnObject<>(ReturnNo.STATENOTALLOW);
            }
        }

        TransactionPattern pattern = transactionPatternFactory.getPatternInstance(refundBill.getPatternId());

        //写进数据库
        Refund refund = cloneVo(refundBill, Refund.class);
        //TODO:要setcreator和modify吗？
        ReturnObject<Refund> retRefund = transactionDao.insertRefund(refund);
        if (!retRefund.getCode().equals(ReturnNo.OK)) {
            return retRefund;
        }
        refund = retRefund.getData();
        //  然后请求退款
        //  创建请求号
        String requestNo = transactionPatternFactory.encodeRequestNo(refund.getId(),
                refund.getDocumentId(), refund.getDocumentType());
        pattern.requestRefund(requestNo, refundBill);

        return new ReturnObject<>(ReturnNo.OK);
    }
    public ReturnObject reconciliation(LocalDateTime beginTime,LocalDateTime endTime){
        //支付宝对账
        TransactionPattern pattern = transactionPatternFactory.getPatternInstance(1L);
        ReturnObject returnObject=pattern.reconciliation(beginTime,endTime);
        if(!returnObject.getCode().equals(ReturnNo.OK)){
            return returnObject;
        }
        ReconciliationRetVo aliPay=(ReconciliationRetVo) returnObject.getData();
        //微信支付对账
        TransactionPattern pattern1 = transactionPatternFactory.getPatternInstance(2L);
        ReturnObject returnObject1=pattern1.reconciliation(beginTime,endTime);
        if(!returnObject1.getCode().equals(ReturnNo.OK)){
            return returnObject1;
        }
        ReconciliationRetVo wechatPay=(ReconciliationRetVo) returnObject1.getData();
        ReconciliationRetVo reconciliationRetVo=new ReconciliationRetVo();
        reconciliationRetVo.setSuccess(aliPay.getSuccess()+wechatPay.getSuccess());
        reconciliationRetVo.setError(aliPay.getError()+wechatPay.getError());
        reconciliationRetVo.setExtra(aliPay.getExtra()+wechatPay.getExtra());
        return new ReturnObject(reconciliationRetVo);
    }



}
