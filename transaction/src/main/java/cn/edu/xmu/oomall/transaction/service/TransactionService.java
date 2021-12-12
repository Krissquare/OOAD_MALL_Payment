package cn.edu.xmu.oomall.transaction.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentState;
import cn.edu.xmu.oomall.transaction.model.vo.*;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
import cn.edu.xmu.oomall.transaction.model.bo.RefundState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    /**
     * gyt
     * 平台管理员查询支付信息
     *
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject listPayment(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        return transactionDao.listPayment(null, documentId, null, state, beginTime, endTime, page, pageSize);

    }

    /**
     * gyt
     * 平台管理员查询支付信息详情
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
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
     * 平台管理员修改支付信息
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
            Payment payment1 = cloneVo(paymentModifyVo, Payment.class);
            payment1.setId(id);
            setPoModifiedFields(payment1, loginUserId, loginUserName);
            ReturnObject returnObject1 = transactionDao.updatePayment(payment1);
            if (!returnObject1.getCode().equals(ReturnNo.OK)) {
                return returnObject1;
            }
            PaymentDetailRetVo paymentDetailRetVo = cloneVo(returnObject1.getData(), PaymentDetailRetVo.class);
            return new ReturnObject(paymentDetailRetVo);
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }

    /**
     * hty
     * 平台管理员查询退款
     *
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject listRefund(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        return transactionDao.listRefund(documentId, state, null,null, beginTime, endTime, page, pageSize);
    }

    /**
     * hty
     * 获取退款详情
     *
     * @param id
     * @return
     */

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
     * 修改退款信息
     *
     * @param id
     * @param refundRecVo
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    public ReturnObject updateRefund(Long id, RefundRecVo refundRecVo, Long loginUserId, String loginUserName) {
        ReturnObject ret = transactionDao.getRefundById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Refund refund1 = (Refund) ret.getData();
        if (refund1.getState() != RefundState.FINISH_REFUND.getCode()) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
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

    public ReturnObject paymentNotifyByWechat(WechatPaymentNotifyVo wechatPaymentNotifyVo) {

        return null;
    }


    public ReturnObject requestPayment(PaymentBill paymentBill, Long loginUserId, String loginUserName) {
        // 根据documentId, documentType去查payment
        ReturnObject<PageInfo<Payment>> retPaymentPageInfo =
                transactionDao.listPayment(paymentBill.getPatternId(), null, paymentBill.getDocumentType(), null, null, null, 1, 100);
        if (!retPaymentPageInfo.getCode().equals(ReturnNo.OK)) {
            return retPaymentPageInfo;
        }

        // 获取paymentList
        Map<String, Object> retMap = (Map<String, Object>) retPaymentPageInfo.getData();
        List<Payment> paymentList = (List<Payment>) retMap.get("list");

        Payment validExistedPayment = null;
        for (Payment payment : paymentList) {
            // 判断是否存在已支付、已对账、已清算的流水
            if (payment.getState().equals(PaymentState.ALREADY_PAY.getCode()) ||
                payment.getState().equals(PaymentState.ALREADY_RECONCILIATION.getCode()) ||
                payment.getState().equals(PaymentState.ALREADY_LIQUIDATION.getCode())) {
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }

            // TODO: 判断是否在beginTime和endTime内

            // 判断是否存在待支付超时流水，不在这里判断
//            if (LocalDateTime.now().isAfter(payment.getEndTime()) &&
//                payment.getState().equals(PaymentState.WAIT_PAY.getCode())) {
//                payment.setState(PaymentState.CANCLE.getCode());
//                setPoModifiedFields(payment, loginUserId, loginUserName);
//
//
//                transactionDao.updatePayment(payment);
//            }

            // 判断是否存在匹配支付渠道的待支付流水
            if (paymentBill.getPatternId().equals(payment.getPatternId()) &&
                payment.getState().equals(PaymentState.WAIT_PAY.getCode())) {
                validExistedPayment = payment;
            }
        }

        // 开始请求支付
        TransactionPattern pattern = transactionPatternFactory.getPatternInstance(paymentBill.getPatternId());
        if (validExistedPayment == null) {
            // 不存在匹配的流水，则需要新建
            Payment payment = cloneVo(paymentBill, Payment.class);
            setPoCreatedFields(payment, loginUserId, loginUserName);
            setPoModifiedFields(payment, loginUserId, loginUserName);
            ReturnObject<Payment> retPayment = transactionDao.insertPayment(payment);

            if (!retPayment.getCode().equals(ReturnNo.OK)) {
                return retPayment;
            }

            // 然后请求支付宝
            ReturnObject ret = pattern.requestPayment(retPayment.getData().getId(), paymentBill);


        } else {
            // 存在匹配的流水，则需要更新
        }

        return null;
    }

}
