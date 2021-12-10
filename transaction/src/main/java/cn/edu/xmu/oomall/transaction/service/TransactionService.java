package cn.edu.xmu.oomall.transaction.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentState;
import cn.edu.xmu.oomall.transaction.model.vo.PaymentDetailRetVo;
import cn.edu.xmu.oomall.transaction.model.vo.PaymentModifyVo;
import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
import cn.edu.xmu.oomall.transaction.model.bo.RefundState;
import cn.edu.xmu.oomall.transaction.model.vo.RefundDetailVo;
import cn.edu.xmu.oomall.transaction.model.vo.RefundRecVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

@Service
public class TransactionService {
    @Autowired
    private TransactionDao transactionDao;

    /**
     * gyt
     * 平台管理员查询支付信息
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject listPayment(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize)
    {
        return transactionDao.listPayment(null,documentId,state,beginTime,endTime,page,pageSize);

    }
    /**
     * gyt
     * 平台管理员查询支付信息详情
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getPaymentDetails(Long id)
    {
        ReturnObject returnObject=transactionDao.getPaymentDetails(id);
        if(!returnObject.getCode().equals(ReturnNo.OK))
        {
            return returnObject;
        }
        PaymentDetailRetVo paymentDetailRetVo=cloneVo(returnObject.getData(),PaymentDetailRetVo.class);
        return new ReturnObject(paymentDetailRetVo);
    }

    /**
     * gyt
     * 平台管理员修改支付信息
     * @param id
     * @param loginUserId
     * @param loginUserName
     * @param paymentModifyVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updatePayment(Long id, Long loginUserId, String loginUserName, PaymentModifyVo paymentModifyVo) {
        ReturnObject returnObject = transactionDao.getPaymentDetails(id);
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

    public ReturnObject listRefund(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize)
    {
        return transactionDao.listRefund(documentId,state,null,beginTime,endTime,page,pageSize);
    }

    public ReturnObject getRefundDetail(Long id)
    {
        ReturnObject ret=transactionDao.getRefundById(id);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return ret;
        }
        RefundDetailVo returnDetailVo=(RefundDetailVo) Common.cloneVo(ret.getData(), RefundDetailVo.class);
        return new ReturnObject(returnDetailVo);
    }

    public ReturnObject updateRefund(Long id, RefundRecVo refundRecVo, Long loginUserId, String loginUserName)
    {
        ReturnObject ret = transactionDao.getRefundById(id);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return ret;
        }
        Refund refund1=(Refund) ret.getData();
        if(refund1.getState()!= RefundState.FINISH_REFUND.getCode())
        {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        refund1.setState(refundRecVo.getState());
        refund1.setDescr(refundRecVo.getDescr());
        Common.setPoModifiedFields(refund1, loginUserId, loginUserName);
        refund1.setAdjustId(loginUserId);
        refund1 .setAdjustName(loginUserName);
        ReturnObject returnObject = transactionDao.updateRefund(refund1);
        if (!returnObject.getCode().equals(ReturnNo.OK.getCode())) {
            return returnObject;
        }
        return new ReturnObject(Common.cloneVo(refund1,RefundDetailVo.class));
    }
}
