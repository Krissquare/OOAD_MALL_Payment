package cn.edu.xmu.oomall.transaction.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.mapper.PaymentPoMapper;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.po.PaymentPo;
import cn.edu.xmu.oomall.transaction.model.po.PaymentPoExample;
import cn.edu.xmu.oomall.transaction.model.vo.PaymentRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class TransactionDao {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);
    @Autowired
    PaymentPoMapper paymentPoMapper;
    public ReturnObject listPayment(Long patternId,String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize)
    {
        try {
            PaymentPoExample example = new PaymentPoExample();
            PaymentPoExample.Criteria criteria = example.createCriteria();
            if(patternId!=null)
            {
                criteria.andPatternIdEqualTo(patternId);
            }
            if(documentId!=null){
                criteria.andDocumentIdEqualTo(documentId);
            }
            if(state!=null)
            {
                criteria.andStateEqualTo(state);
            }
            if(beginTime!=null){
                criteria.andPayTimeGreaterThan(beginTime);
            }
            if(endTime!=null){
                criteria.andPayTimeLessThan(endTime);
            }
            PageHelper.startPage(page, pageSize);
            List<PaymentPo> list = paymentPoMapper.selectByExample(example);
            PageInfo pageInfo = new PageInfo(list);
            ReturnObject pageRetVo = Common.getPageRetVo(new ReturnObject<>(pageInfo), PaymentRetVo.class);
            return pageRetVo;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    public ReturnObject getPaymentDetails(Long id)
    {
        try{
            PaymentPo paymentPo=paymentPoMapper.selectByPrimaryKey(id);
            if(paymentPo==null)
            {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            //TODO:redis
            return new ReturnObject(cloneVo(paymentPo,Payment.class));
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }

    /**
     * 平台管理员修改支付信息
     * @param payment
     * @return
     */
    public ReturnObject updatePayment(Payment payment)
    {
        try {
            PaymentPo paymentPo=cloneVo(payment,PaymentPo.class);
            paymentPoMapper.updateByPrimaryKeySelective(paymentPo);
            //TODO:删除redis
            Payment payment1=cloneVo(paymentPo,Payment.class);
            return new ReturnObject(payment1);

        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
}
