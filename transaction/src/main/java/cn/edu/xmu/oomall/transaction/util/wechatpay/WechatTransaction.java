package cn.edu.xmu.oomall.transaction.util.wechatpay;

import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.WechatMicroService;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatPaymentVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatRefundVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.WechatPaymentRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class WechatTransaction extends TransactionPattern {

    @Autowired
    private WechatMicroService wechatMicroService;


    private static WechatPaymentVo createWechatRequestPaymentVo(Long requestNo, PaymentBill bill) {
        WechatPaymentVo paymentVo = new WechatPaymentVo();

        paymentVo.setOutTradeNo(requestNo.toString());
        paymentVo.getAmount().setTotal(bill.getAmount());

        return paymentVo;
    }

    private static WechatRefundVo createWechatRequestRefundVo(Long requestNo, RefundBill bill) {
        WechatRefundVo refundVo = new WechatRefundVo();
        refundVo.setOutRefundNo(requestNo.toString());
        refundVo.setOutTradeNo(bill.getPaymentId().toString());
        refundVo.setReason(bill.getReason());
        refundVo.getAmount().setRefund(bill.getAmount());
        refundVo.getAmount().setTotal(bill.getTotal());
        return refundVo;
    }


    @Override
    public void requestPayment(Long requestNo, PaymentBill bill) {
        WechatPaymentVo paymentVo = WechatTransaction.createWechatRequestPaymentVo(requestNo, bill);
        WechatPaymentRetVo ret = wechatMicroService.requestPayment(paymentVo);


    }

    @Override
    public void requestRefund(Long requestNo, RefundBill bill) {
        WechatRefundVo refundVo = WechatTransaction.createWechatRequestRefundVo(requestNo, bill);
        InternalReturnObject ret = wechatMicroService.requestRefund(refundVo);

    }
}
