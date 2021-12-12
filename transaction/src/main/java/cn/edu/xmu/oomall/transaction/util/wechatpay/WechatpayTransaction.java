package cn.edu.xmu.oomall.transaction.util.wechatpay;

import cn.edu.xmu.oomall.core.util.ReturnObject;

import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatpayPaymentVo;
import org.springframework.stereotype.Component;

@Component
public class WechatpayTransaction extends TransactionPattern {

    private static WechatpayPaymentVo createWechatpayPaymentVo(Long requestNo, PaymentBill bill) {
        WechatpayPaymentVo paymentVo = new WechatpayPaymentVo();
        paymentVo.setOutTradeNo(requestNo.toString());
        paymentVo.getAmount().setTotal(bill.getAmount());

        return paymentVo;
    }


    @Override
    public ReturnObject requestPayment(Long requestNo, PaymentBill bill) {
        WechatpayPaymentVo paymentVo = WechatpayTransaction.createWechatpayPaymentVo(requestNo, bill);

    }
}
