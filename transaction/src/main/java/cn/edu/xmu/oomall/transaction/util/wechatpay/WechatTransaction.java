package cn.edu.xmu.oomall.transaction.util.wechatpay;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentState;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
import cn.edu.xmu.oomall.transaction.model.bo.RefundState;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.AlipayRefundQueryRetVo;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.AlipayRefundQueryVo;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayMethod;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayRefundState;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayTradeState;
import cn.edu.xmu.oomall.transaction.util.mq.ActiveQueryMessage;
import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.WechatMicroService;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.*;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatRefundState;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatReturnNo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatReturnObject;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatTradeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class WechatTransaction extends TransactionPattern {

    @Autowired
    private WechatMicroService wechatMicroService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;


    @Override
    public void requestPayment(String requestNo, PaymentBill bill) {
        WechatPaymentVo paymentVo = new WechatPaymentVo();
        paymentVo.setOutTradeNo(requestNo);
        paymentVo.getAmount().setTotal(bill.getAmount());

     //   WechatPaymentRetVo ret = wechatMicroService.requestPayment(paymentVo);
        WechatReturnObject<WechatPaymentRetVo> wechatReturnObject = wechatMicroService.requestPayment(paymentVo);

        // prepayId暂时没用
        // 发送主动查询支付的延时消息
        ActiveQueryMessage queryMessage = new ActiveQueryMessage();
        queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERY_PAYMENT);
        queryMessage.setRequestNo(requestNo);
        queryMessage.setPatternId(bill.getPatternId());
        queryMessage.setBill(bill);
        messageProducer.sendActiveQueryDelayedMessage(queryMessage);
    }

    @Override
    public void requestRefund(String requestNo, RefundBill bill) {
        WechatRefundVo refundVo = new WechatRefundVo();
        refundVo.setOutRefundNo(requestNo);
        refundVo.setOutTradeNo(bill.getPaymentId().toString());
        refundVo.setReason(bill.getReason());
        refundVo.getAmount().setRefund(bill.getAmount());
        refundVo.getAmount().setTotal(bill.getTotal());

       WechatReturnObject<WechatRefundRetVo> ret = wechatMicroService.requestRefund(refundVo);


       if (ret != null) {
           WechatRefundRetVo wechatRefundRetVo = ret.getData();
           Refund refund = new Refund();
           if (wechatRefundRetVo.getStatus().equals(WechatRefundState.SUCCESS.getState())) {
               refund.setState(RefundState.FINISH_REFUND.getCode());
           } else {
               refund.setState(RefundState.FAILED.getCode());
           }
           String refundId = (String) transactionPatternFactory
                   .decodeRequestNo(requestNo).get("id");
           refund.setId(Long.parseLong(refundId));
           refund.setRefundTime(wechatRefundRetVo.getSuccessTime());
           refund.setTradeSn(wechatRefundRetVo.getTransactionId());
           transactionDao.updateRefund(refund);
       } else {
           // 未接受到数据的情况下
           // 发送主动查询退款的延时消息
           ActiveQueryMessage queryMessage = new ActiveQueryMessage();
           queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERT_REFUND);
           queryMessage.setRequestNo(requestNo);
           queryMessage.setOutTradeNo(bill.getPaymentId().toString());
           queryMessage.setPatternId(bill.getPatternId());
           // 怎么延时？
           messageProducer.sendActiveQueryDelayedMessage(queryMessage);
       }
    }

    @Override
    public void queryPayment(String requestNo, PaymentBill bill) {
        String paymentId = (String) transactionPatternFactory
                .decodeRequestNo(requestNo).get("id");

        ReturnObject<Payment> retPayment = transactionDao.getPaymentById(Long.parseLong(paymentId));
        if (retPayment.getCode().equals(ReturnNo.OK)) {
            // 待支付的情况下，才主动查询
            if (retPayment.getData().getState().equals(PaymentState.WAIT_PAY.getCode())) {
                {
                    WechatReturnObject<WechatPaymentQueryRetVo> wechatReturnObject
                            = wechatMicroService.queryPayment(requestNo);
                    WechatPaymentQueryRetVo wechatPaymentQueryRetVo = wechatReturnObject.getData();
                    Payment payment = new Payment();
                    if (wechatPaymentQueryRetVo.getTradeState().equals(WechatTradeState.NOTPAY.getState())) {
                        // 发送主动查询支付的延时消息
                        ActiveQueryMessage queryMessage = new ActiveQueryMessage();
                        queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERY_PAYMENT);
                        queryMessage.setRequestNo(requestNo);
                        queryMessage.setPatternId(bill.getPatternId());
                        messageProducer.sendActiveQueryDelayedMessage(queryMessage);
                    }
                    else if (wechatPaymentQueryRetVo.getTradeState().equals(WechatTradeState.CLOSED.getState())) {
                        // 已关闭
                        payment.setState(PaymentState.CANCEL.getCode());
                    } else if (wechatPaymentQueryRetVo.getTradeState().equals(WechatTradeState.SUCCESS.getState())) {
                        // 成功
                        payment.setState(PaymentState.ALREADY_PAY.getCode());
                    }

                    payment.setId(Long.parseLong(paymentId));
                    payment.setPayTime(wechatPaymentQueryRetVo.getSuccessTime());
                    payment.setTradeSn(wechatPaymentQueryRetVo.getTransactionId());
                    payment.setActualAmount(wechatPaymentQueryRetVo.getAmount().getPayerTotal());
                    transactionDao.updatePayment(payment);
                }
            }
        }
    }

    @Override
    public void queryRefund(String requestNo, RefundBill bill) {
        String refundId = (String) transactionPatternFactory
                .decodeRequestNo(requestNo).get("id");
        ReturnObject<Refund> retRefund = transactionDao.getRefundById(Long.parseLong(refundId));

        if (retRefund.getCode().equals(ReturnNo.OK)) {
            if (retRefund.getData().getState().equals(RefundState.WAIT_REFUND.getCode())) {
                WechatReturnObject<WechatRefundQueryRetVo> wechatReturnObject = wechatMicroService.queryRefund(requestNo);
                WechatRefundQueryRetVo wechatRefundQueryRetVo = wechatReturnObject.getData();
                Refund refund = new Refund();
                if (wechatRefundQueryRetVo.getStatus().equals(WechatRefundState.SUCCESS.getState())) {
                    refund.setState(RefundState.FINISH_REFUND.getCode());
                } else {
                    refund.setState(RefundState.FAILED.getCode());
                }

                refund.setId(Long.parseLong(refundId));
                refund.setRefundTime(wechatRefundQueryRetVo.getSuccessTime());
                refund.setTradeSn(wechatRefundQueryRetVo.getTransactionId());
                transactionDao.updateRefund(refund);
            }
        }
    }
}