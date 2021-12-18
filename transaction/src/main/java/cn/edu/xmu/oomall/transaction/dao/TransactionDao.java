package cn.edu.xmu.oomall.transaction.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.mapper.PaymentPatternPoMapper;
import cn.edu.xmu.oomall.transaction.mapper.ErrorAccountPoMapper;
import cn.edu.xmu.oomall.transaction.mapper.PaymentPatternPoMapper;
import cn.edu.xmu.oomall.transaction.mapper.PaymentPoMapper;
import cn.edu.xmu.oomall.transaction.model.bo.ErrorAccount;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentPattern;
import cn.edu.xmu.oomall.transaction.model.po.*;
import cn.edu.xmu.oomall.transaction.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.xmu.oomall.transaction.mapper.RefundPoMapper;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class TransactionDao {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private PaymentPoMapper paymentPoMapper;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private RefundPoMapper refundPoMapper;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private PaymentPatternPoMapper paymentPatternPoMapper;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ErrorAccountPoMapper errorAccountPoMapper;

    @Autowired
    RedisUtil redisUtil;

    @Value("${oomall.transaction.expiretime}")
    private long transactionExpireTime;

    final static String PAYMENT_KEY = "payment_%d";
    final static String REFUND_KEY = "refund_%d";
    final static String PATTERN_KEY = "pattern_%d";

    public ReturnObject insertPayment(Payment payment) {
        try {
            PaymentPo po = cloneVo(payment, PaymentPo.class);
            paymentPoMapper.insert(po);
            return new ReturnObject<>(cloneVo(po, Payment.class));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listPayment(Long patternId, String documentId, Byte documentType, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        try {
            PaymentPoExample example = new PaymentPoExample();
            PaymentPoExample.Criteria criteria = example.createCriteria();
            if (patternId != null) {
                criteria.andPatternIdEqualTo(patternId);
            }
            if (documentId != null) {
                criteria.andDocumentIdEqualTo(documentId);
            }
            if (documentType != null) {
                criteria.andDocumentTypeEqualTo(documentType);
            }
            if (state != null) {
                criteria.andStateEqualTo(state);
            }
            if (beginTime != null) {
                criteria.andPayTimeGreaterThan(beginTime);
            }
            if (endTime != null) {
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


    public ReturnObject getPaymentById(Long id) {
        try {
            String key = String.format(PAYMENT_KEY, id);
            Payment payment = (Payment) redisUtil.get(key);
            if (payment != null) {
                return new ReturnObject(payment);
            }
            PaymentPo paymentPo = paymentPoMapper.selectByPrimaryKey(id);
            if (paymentPo == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            payment = cloneVo(paymentPo, Payment.class);
            redisUtil.set(key, payment, transactionExpireTime);
            return new ReturnObject(payment);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }

    /**
     * 平台管理员修改支付信息
     *
     * @param payment
     * @return
     */
    public ReturnObject updatePayment(Payment payment) {
        try {
            PaymentPo paymentPo = cloneVo(payment, PaymentPo.class);
            int flag = paymentPoMapper.updateByPrimaryKeySelective(paymentPo);
            if (flag == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                redisUtil.del(String.format(PAYMENT_KEY, payment.getId()));
                return new ReturnObject(ReturnNo.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listRefund(Long paymentId, String documentId, Byte state, Byte documentType, Long patternId, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        try {
            PageHelper.startPage(page, pageSize);
            RefundPoExample refundPoExample = new RefundPoExample();
            RefundPoExample.Criteria cr = refundPoExample.createCriteria();
            if (paymentId != null) {
                cr.andPaymentIdEqualTo(paymentId);
            }
            if (documentId != null) {
                cr.andDocumentIdEqualTo(documentId);
            }
            if (state != null) {
                cr.andStateEqualTo(state);
            }
            if (documentType != null) {
                cr.andDocumentTypeEqualTo(documentType);
            }
            if (patternId != null) {
                cr.andPatternIdEqualTo(patternId);
            }
            if (beginTime != null) {
                cr.andRefundTimeGreaterThan(beginTime);
            }
            if (endTime != null) {
                cr.andRefundTimeLessThan(endTime);
            }
            List<RefundPo> refundPoList = refundPoMapper.selectByExample(refundPoExample);
            if (refundPoList.size() == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo(refundPoList));
            return Common.getPageRetVo(ret, RefundRetVo.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject getRefundById(Long id) {
        try {
            String key = String.format(REFUND_KEY, id);
            Refund refund = (Refund) redisUtil.get(key);
            if (refund != null) {
                return new ReturnObject(refund);
            }
            RefundPo refundPo = refundPoMapper.selectByPrimaryKey(id);
            if (refundPo == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            refund = cloneVo(refundPo, Refund.class);
            redisUtil.set(key, refund, transactionExpireTime);
            return new ReturnObject(refund);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject updateRefund(Refund refund) {
        try {
            RefundPo refundPo = cloneVo(refund, RefundPo.class);
            int flag = refundPoMapper.updateByPrimaryKeySelective(refundPo);
            if (flag == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                redisUtil.del(String.format(REFUND_KEY, refund.getId()));
                return new ReturnObject(ReturnNo.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject insertRefund(Refund refund) {
        try {
            RefundPo refundPo = cloneVo(refund, RefundPo.class);
            refundPoMapper.insertSelective(refundPo);
            return new ReturnObject(cloneVo(refundPo, Refund.class));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getPaymentPatternById(Long id) {
        try {
            String key = String.format(PATTERN_KEY, id);
            PaymentPattern paymentPattern = (PaymentPattern) redisUtil.get(key);
            if (paymentPattern != null) {
                return new ReturnObject(paymentPattern);
            }
            PaymentPatternPo po = paymentPatternPoMapper.selectByPrimaryKey(id);
            if (po == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            paymentPattern = cloneVo(po, PaymentPattern.class);
            redisUtil.set(key, paymentPattern, transactionExpireTime);
            return new ReturnObject(paymentPattern);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listAllPayPattern() {
        try {
            PaymentPatternPoExample paymentPatternPoExample = new PaymentPatternPoExample();
            List<PaymentPatternPo> validPayPatterns = paymentPatternPoMapper.selectByExample(paymentPatternPoExample);
            return new ReturnObject(validPayPatterns);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listErrorAccountsVoByConditions(String documentId,
                                                        Byte state,
                                                        LocalDateTime beginTime,
                                                        LocalDateTime endTime,
                                                        Integer page,
                                                        Integer pageSize) {
        try {
            ErrorAccountPoExample errorAccountPoExample = new ErrorAccountPoExample();
            ErrorAccountPoExample.Criteria criteria = errorAccountPoExample.createCriteria();
            if (documentId != null) {
                criteria.andDocumentIdEqualTo(documentId);
            }
            if (state != null) {
                criteria.andStateEqualTo(state);
            }
            if (beginTime != null) {
                criteria.andTimeGreaterThanOrEqualTo(beginTime);
            }
            if (beginTime != null) {
                criteria.andTimeLessThanOrEqualTo(endTime);
            }
            PageHelper.startPage(page, pageSize, true, true, true);
            List<ErrorAccountPo> errorAccountPoList = errorAccountPoMapper.selectByExample(errorAccountPoExample);
            ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo<>(errorAccountPoList));
            return Common.getPageRetVo(ret, ErrorAccountVo.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getErrorAccount(Long id) {
        try {
            ErrorAccountPo errorAccountPo = errorAccountPoMapper.selectByPrimaryKey(id);
            return new ReturnObject(errorAccountPo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject updateErrorAccount(ErrorAccountPo errorAccountPo) {
        try {
            int flag = errorAccountPoMapper.updateByPrimaryKeySelective(errorAccountPo);
            if (flag == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return new ReturnObject(ReturnNo.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    public ReturnObject getPaymentByTradeSn(String tradeSn){
        try {
            PaymentPoExample example = new PaymentPoExample();
            PaymentPoExample.Criteria criteria = example.createCriteria();
            criteria.andTradeSnEqualTo(tradeSn);
            List<PaymentPo> paymentPos=paymentPoMapper.selectByExample(example);
            if (paymentPos.size()==0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            PaymentPo paymentPo=paymentPos.get(0);
            Payment payment= cloneVo(paymentPo, Payment.class);
            return new ReturnObject(payment);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    public ReturnObject getRefundByTradeSn(String tradeSn){
        try {
            RefundPoExample example = new RefundPoExample();
            RefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andTradeSnEqualTo(tradeSn);
            List<RefundPo> refundPos=refundPoMapper.selectByExample(example);
            if (refundPos.size()==0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            RefundPo refundPo=refundPos.get(0);
            Refund refund=cloneVo(refundPo,Refund.class);
            return new ReturnObject(refund);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    public ReturnObject insertErrorAccount(ErrorAccount errorAccount){
        try {
            ErrorAccountPo errorAccountPo=cloneVo(errorAccount,ErrorAccountPo.class);
            errorAccountPoMapper.insert(errorAccountPo);
            return new ReturnObject<>(cloneVo(errorAccountPo, ErrorAccount.class));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }



}
