package cn.edu.xmu.oomall.transaction.util.alipay;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.AlipayMicroService;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.bo.AlipayMethod;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.AlipayPaymentRetVo;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.AlipayPaymentVo;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.AlipayRefundRetVo;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.AlipayRefundVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlipayTransaction extends TransactionPattern {

    @Autowired
    private AlipayMicroService alipayMicroService;

    private static AlipayPaymentVo createAlipayPaymentVo(Long requestNo, PaymentBill bill) {
        AlipayPaymentVo paymentVo = new AlipayPaymentVo();
        paymentVo.setOutTradeNo(requestNo.toString());
        paymentVo.setTotalAmount(bill.getAmount());

        return paymentVo;
    }

    private static AlipayRefundVo createAlipayRefundVo(Long requestNo, RefundBill bill) {
        AlipayRefundVo refundVo = new AlipayRefundVo();
        refundVo.setOutTradeNo(bill.getPaymentId().toString());
        refundVo.setOutRequestNo(requestNo.toString());
        refundVo.setRefundAmount(bill.getAmount());

        return refundVo;
    }


    @Override
    public void requestPayment(Long requestNo, PaymentBill bill) {
        AlipayPaymentVo paymentVo = createAlipayPaymentVo(requestNo, bill);
        AlipayPaymentRetVo retVo = (AlipayPaymentRetVo) alipayMicroService.gatewayDo(null,
                AlipayMethod.PAY.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(paymentVo));

    }

    @Override
    public void requestRefund(Long requestNo, RefundBill bill) {
        AlipayRefundVo refundVo = createAlipayRefundVo(requestNo, bill);
        AlipayRefundRetVo retVo = (AlipayRefundRetVo) alipayMicroService.gatewayDo(null,
                AlipayMethod.PAY.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(refundVo));

    }

}
