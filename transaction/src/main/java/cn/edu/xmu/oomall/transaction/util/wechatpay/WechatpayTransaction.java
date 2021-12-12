package cn.edu.xmu.oomall.transaction.util.wechatpay;

import cn.edu.xmu.oomall.core.util.ReturnObject;

import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import org.springframework.stereotype.Component;

@Component
public class WechatpayTransaction extends TransactionPattern {


    @Override
    public ReturnObject requestPayment(PaymentBill bill) {
        return null;
    }
}
