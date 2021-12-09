package cn.edu.xmu.oomall.ooad201.payment.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.payment.mapper.ErrorAccountPoMapper;
import cn.edu.xmu.oomall.ooad201.payment.mapper.PaymentPatternPoMapper;
import cn.edu.xmu.oomall.ooad201.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.oomall.ooad201.payment.mapper.RefundPoMapper;
import cn.edu.xmu.oomall.ooad201.payment.model.bo.Refund;
import cn.edu.xmu.oomall.ooad201.payment.model.bo.RefundState;
import cn.edu.xmu.oomall.ooad201.payment.model.po.RefundPo;
import cn.edu.xmu.oomall.ooad201.payment.model.po.RefundPoExample;
import cn.edu.xmu.oomall.ooad201.payment.model.vo.RefundDetailVo;
import cn.edu.xmu.oomall.ooad201.payment.model.vo.RefundRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

@Repository
public class PaymentDao {
    @Autowired
    ErrorAccountPoMapper errorAccountPoMapper;
    @Autowired
    PaymentPatternPoMapper paymentPatternPoMapper;
    @Autowired
    PaymentPoMapper paymentPoMapper;
    @Autowired
    RefundPoMapper refundPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    public ReturnObject getRefund(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize)
    {
        try
        {
            PageHelper.startPage(page, pageSize, true, true, true);
            RefundPoExample refundPoExample = new RefundPoExample();
            RefundPoExample.Criteria cr = refundPoExample.createCriteria();
            if(documentId!=null)
            {
                cr.andDocumentIdEqualTo(documentId);
            }
            if(state!=null)
            {
                cr.andStateEqualTo(state);
            }
            if(beginTime!=null)
            {
                cr.andRefundTimeGreaterThan(beginTime);
            }
            if(endTime!=null)
            {
                cr.andRefundTimeLessThan(endTime);
            }
            List<RefundPo> refundPoList = refundPoMapper.selectByExample(refundPoExample);
            if (refundPoList.size() == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo(refundPoList));
            return Common.getPageRetVo(ret, RefundRetVo.class);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getRefundDetail(Long id)
    {
        try
        {
            RefundPo refundPo=refundPoMapper.selectByPrimaryKey(id);
            if(refundPo==null)
            {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject((Refund)Common.cloneVo(refundPo, Refund.class));
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject updateRefund(Refund refund)
    {
        RefundPo refundPo  =(RefundPo)Common.cloneVo(refund,RefundPo.class);
        int flag = refundPoMapper.updateByPrimaryKeySelective(refundPo);
        if (flag == 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        } else {
            return new ReturnObject(ReturnNo.OK);
        }
    }


}
