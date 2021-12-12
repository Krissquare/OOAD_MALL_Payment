package cn.edu.xmu.oomall.transaction.util.wechatpay;

import cn.edu.xmu.oomall.core.util.ReturnObject;

import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatPaymentVo;
import org.springframework.stereotype.Component;

@Component
public class WechatTransaction extends TransactionPattern {

    private static WechatPaymentVo createWechatpayPaymentVo(Long requestNo, PaymentBill bill) {
        WechatPaymentVo paymentVo = new WechatPaymentVo();
        paymentVo.setOutTradeNo(requestNo.toString());
        paymentVo.getAmount().setTotal(bill.getAmount());

        return paymentVo;
    }


    @Override
    public ReturnObject requestPayment(Long requestNo, PaymentBill bill) {
        WechatPaymentVo paymentVo = WechatTransaction.createWechatpayPaymentVo(requestNo, bill);

        return null;
    }
}
