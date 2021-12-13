package cn.edu.xmu.oomall.transaction.util;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.util.alipay.AlipayTransaction;
import cn.edu.xmu.oomall.transaction.util.wechatpay.WechatTransaction;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionPatternFactory {

    @Autowired
    private AlipayTransaction alipayTransaction;

    @Autowired
    private WechatTransaction wechatpayTransaction;

    @Autowired
    private TransactionDao transactionDao;

    public TransactionPattern getPatternInstance(Long patternId) {
        ReturnObject<PaymentPattern> retPaymentPattern = transactionDao.getPaymentPatternById(patternId);
        if (retPaymentPattern.getCode().equals(ReturnNo.OK)) {
//            throw new ClassNotFoundException();
            return null;
        }

        PaymentPattern paymentPattern = retPaymentPattern.getData();

        if (paymentPattern.getId() == 1) {
            return wechatpayTransaction;
        } else {
            return alipayTransaction;
        }
        // TODO: 利用Spring技术获取Bean对象

    }

}
