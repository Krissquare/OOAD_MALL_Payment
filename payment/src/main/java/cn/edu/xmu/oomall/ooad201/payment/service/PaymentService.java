package cn.edu.xmu.oomall.ooad201.payment.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.payment.dao.PaymentDao;
import cn.edu.xmu.oomall.ooad201.payment.model.bo.Refund;
import cn.edu.xmu.oomall.ooad201.payment.model.bo.RefundState;
import cn.edu.xmu.oomall.ooad201.payment.model.vo.RefundDetailVo;
import cn.edu.xmu.oomall.ooad201.payment.model.vo.RefundRecVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    @Autowired
    PaymentDao paymentDao;

    public ReturnObject getRefund( String documentId, Byte state, LocalDateTime beginTime,LocalDateTime endTime, Integer page, Integer pageSize)
    {
        return paymentDao.getRefund(documentId,state,beginTime,endTime,page,pageSize);
    }

    public ReturnObject getRefundDetail(Long id)
    {
        ReturnObject ret=paymentDao.getRefundDetail(id);
        if(ret.getData()==null)
        {
            return ret;
        }
        RefundDetailVo returnDetailVo=(RefundDetailVo) Common.cloneVo(ret.getData(), RefundDetailVo.class);
        return new ReturnObject(returnDetailVo);
    }
    public ReturnObject updateRefund( Long id, RefundRecVo refundRecVo,Long loginUserId, String loginUserName)
    {

        ReturnObject ret=paymentDao.getRefundDetail(id);
        if(ret.getData()==null)
        {
            return ret;
        }
        Refund refund1=(Refund) ret.getData();
        if(refund1.getState()!= RefundState.FINISH_REFUND.getCode())
        {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        refund1.setState(refundRecVo.getState());
        refund1.setDescr(refundRecVo.getDescr());
        Common.setPoModifiedFields(refund1, loginUserId, loginUserName);
        refund1.setAdjustId(loginUserId);
        refund1 .setAdjustName(loginUserName);
        ReturnObject returnObject = paymentDao.updateRefund(refund1);
        if (!returnObject.getCode().equals(ReturnNo.OK.getCode())) {
            return returnObject;
        }
        return new ReturnObject((RefundDetailVo)Common.cloneVo(refund1,RefundDetailVo.class));
    }
}
