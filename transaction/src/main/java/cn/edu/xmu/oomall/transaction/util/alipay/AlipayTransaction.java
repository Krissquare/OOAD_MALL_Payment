package cn.edu.xmu.oomall.transaction.util.alipay;

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
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.AlipayMicroService;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.*;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayMethod;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayRefundState;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayReturnNo;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayTradeState;
import cn.edu.xmu.oomall.transaction.util.mq.ActiveQueryMessage;
import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlipayTransaction extends TransactionPattern {

    @Autowired
    private AlipayMicroService alipayMicroService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;


    @Override
    public void requestPayment(String requestNo, PaymentBill bill) {
        AlipayPaymentVo paymentVo = new AlipayPaymentVo();
        paymentVo.setOutTradeNo(requestNo);
        paymentVo.setTotalAmount(bill.getAmount());

        AlipayPaymentRetVo alipayPaymentRetVo = (AlipayPaymentRetVo) alipayMicroService.gatewayDo(null,
                AlipayMethod.QUERY_PAY.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(paymentVo));

        ActiveQueryMessage queryMessage = new ActiveQueryMessage();
        queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERY_PAYMENT);
        queryMessage.setRequestNo(requestNo);
        queryMessage.setPatternId(bill.getPatternId());
        queryMessage.setBill(bill);
        messageProducer.sendActiveQueryDelayedMessage(queryMessage);
    }

    @Override
    public void requestRefund(String requestNo, RefundBill bill) {
        AlipayRefundVo refundVo = new AlipayRefundVo();
        refundVo.setOutTradeNo(bill.getPaymentId().toString());
        refundVo.setOutRequestNo(requestNo);
        refundVo.setRefundAmount(bill.getAmount());

        AlipayRefundRetVo alipayRefundRetVo = (AlipayRefundRetVo) alipayMicroService.gatewayDo(null,
                AlipayMethod.QUERY_REFUND.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(refundVo));

        // 同步处理，在支持退款的情况下
//        if (alipayRefundRetVo != null
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.REFUND_AMT_NOT_EQUAL_TOTAL.getSubCode())
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.TRADE_NOT_ALLOW_REFUND.getSubCode())
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.TRADE_NOT_EXIST.getSubCode())) {
// 同步回传没有成功时间，还是靠主动查询吧
//        }


        // 发送主动查询退款的延时消息
        ActiveQueryMessage queryMessage = new ActiveQueryMessage();
        queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERT_REFUND);
        queryMessage.setRequestNo(requestNo);
        queryMessage.setOutTradeNo(bill.getPaymentId().toString());
        queryMessage.setPatternId(bill.getPatternId());
        queryMessage.setBill(bill);
        messageProducer.sendActiveQueryDelayedMessage(queryMessage);
    }


    @Override
    public void queryPayment(String requestNo, PaymentBill bill) {
        String paymentId = (String) transactionPatternFactory
                .decodeRequestNo(requestNo).get("id");

        ReturnObject<Payment> retPayment = transactionDao.getPaymentById(Long.parseLong(paymentId));
        if (retPayment.getCode().equals(ReturnNo.OK)) {
            // 待支付的情况下，才主动查询
            if (retPayment.getData().getState().equals(PaymentState.WAIT_PAY.getCode())) {
                AlipayPaymentQueryVo queryVo = new AlipayPaymentQueryVo();
                queryVo.setOutTradeNo(requestNo);

                AlipayPaymentQueryRetVo alipayPaymentQueryRetVo = (AlipayPaymentQueryRetVo) alipayMicroService.gatewayDo(null,
                        AlipayMethod.QUERY_PAY.getMethod(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JacksonUtil.toJson(queryVo));

                Payment payment = new Payment();
                if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.WAIT_BUYER_PAY.getDescription())) {
                    // 发送主动查询支付的延时消息
                    ActiveQueryMessage queryMessage = new ActiveQueryMessage();
                    queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERY_PAYMENT);
                    queryMessage.setRequestNo(requestNo);
                    queryMessage.setPatternId(bill.getPatternId());
                    messageProducer.sendActiveQueryDelayedMessage(queryMessage);
                }
                else if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
                    // 已关闭
                    payment.setState(PaymentState.CANCEL.getCode());
                } else if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                        alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
                    // 交易结束
                    // 成功
                    payment.setState(PaymentState.ALREADY_PAY.getCode());
                }

                payment.setId(Long.parseLong(paymentId));
                payment.setPayTime(alipayPaymentQueryRetVo.getSendPayDate());
                payment.setTradeSn(alipayPaymentQueryRetVo.getTradeNo());
                payment.setActualAmount(alipayPaymentQueryRetVo.getBuyerPayAmount());
                transactionDao.updatePayment(payment);
            }
        }
    }

    @Override
    public void queryRefund(String requestNo , RefundBill bill) {

        String refundId = (String) transactionPatternFactory
                .decodeRequestNo(requestNo).get("id");
        ReturnObject<Refund> retRefund = transactionDao.getRefundById(Long.parseLong(refundId));

        if (retRefund.getCode().equals(ReturnNo.OK)) {
            if (retRefund.getData().getState().equals(RefundState.WAIT_REFUND.getCode())) {
                AlipayRefundQueryVo queryVo = new AlipayRefundQueryVo();
                queryVo.setOutRequestNo(requestNo);
                queryVo.setOutTradeNo(bill.getPaymentId().toString());

                AlipayRefundQueryRetVo alipayRefundQueryRetVo = (AlipayRefundQueryRetVo) alipayMicroService.gatewayDo(null,
                        AlipayMethod.QUERY_REFUND.getMethod(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JacksonUtil.toJson(queryVo));

                Refund refund = new Refund();
                if (alipayRefundQueryRetVo.getRefundStatus().equals(AlipayRefundState.REFUND_SUCCESS.getDescription())) {
                    refund.setState(RefundState.FINISH_REFUND.getCode());
                } else {
                    refund.setState(RefundState.FAILED.getCode());
                }

                refund.setId(Long.parseLong(refundId));
                refund.setRefundTime(alipayRefundQueryRetVo.getGmtRefundPay());
                refund.setTradeSn(alipayRefundQueryRetVo.getTradeNo());
                transactionDao.updateRefund(refund);
            }
        }
    }
}
