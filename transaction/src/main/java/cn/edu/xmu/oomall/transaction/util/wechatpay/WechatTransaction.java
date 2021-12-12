package cn.edu.xmu.oomall.transaction.util.wechatpay;

import cn.edu.xmu.oomall.core.util.ReturnObject;

import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.WechatMicroService;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatRequestPaymentVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatRequestRefundVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WechatTransaction extends TransactionPattern {

    @Autowired
    private WechatMicroService wechatMicroService;


    private static WechatRequestPaymentVo createWechatRequestPaymentVo(Long requestNo, PaymentBill bill) {
        WechatRequestPaymentVo paymentVo = new WechatRequestPaymentVo();

        // 商家号那些还没

        paymentVo.setOutTradeNo(requestNo.toString());
        paymentVo.getAmount().setTotal(bill.getAmount());

        return paymentVo;
    }

    private static WechatRequestRefundVo createWechatRequestRefundVo(Long requestNo, RefundBill bill) {
        WechatRequestRefundVo refundVo = new WechatRequestRefundVo();
        // 商家号那些都不用吗？
        refundVo.setOutRefundNo(requestNo.toString());
        refundVo.setOutTradeNo(bill.getPaymentId());
        refundVo.setReason(bill.getReason());
    //    refundVo.getAmount().setTotal();

        return refundVo;
    }



    @Override
    public ReturnObject requestPayment(Long requestNo, PaymentBill bill) {
        WechatRequestPaymentVo paymentVo = WechatTransaction.createWechatRequestPaymentVo(requestNo, bill);

        InternalReturnObject ret = wechatMicroService.requestPayment(paymentVo);

        return null;
    }

    @Override
    public ReturnObject requestRefund(Long requestNo, RefundBill bill) {
        WechatRequestRefundVo refundVo = WechatTransaction.createWechatRequestRefundVo(requestNo, bill);

        InternalReturnObject ret = wechatMicroService.requestRefund(refundVo);

        return null;

    }
}
