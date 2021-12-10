package cn.edu.xmu.oomall.transaction.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentState;
import cn.edu.xmu.oomall.transaction.model.vo.PaymentDetailRetVo;
import cn.edu.xmu.oomall.transaction.model.vo.PaymentModifyVo;
import cn.edu.xmu.oomall.transaction.model.vo.WechatPaymentNotifyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import java.time.LocalDateTime;
@Service
public class TransactionService {
    @Autowired
    TransactionDao transactionDao;

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
        if(returnObject.getData()==null)
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
        if (returnObject.getData() == null) {
            return returnObject;
        }
        Payment payment = (Payment) returnObject.getData();
        if (payment.getState().equals(PaymentState.ALREADY_PAY.getCode()) || payment.getState().equals(PaymentState.FAIL.getCode())) {
            Payment payment1 = cloneVo(paymentModifyVo, Payment.class);
            payment1.setId(id);
            setPoModifiedFields(payment1, loginUserId, loginUserName);
            ReturnObject returnObject1 = transactionDao.updatePayment(payment1);
            if (returnObject1.getData() == null) {
                return returnObject1;
            }
            PaymentDetailRetVo paymentDetailRetVo = cloneVo(returnObject1.getData(), PaymentDetailRetVo.class);
            return new ReturnObject(paymentDetailRetVo);
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }

    public Object paymentNotifyByWechat(WechatPaymentNotifyVo wechatPaymentNotifyVo){
        String out_trade_no = wechatPaymentNotifyVo.getResource().getCiphertext().getOut_trade_no();//订单号
//        transactionDao.listPayment()

        return null;
    }


}
