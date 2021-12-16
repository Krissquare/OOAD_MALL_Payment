package cn.edu.xmu.oomall.transaction.util.alipay.service;

import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;

import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import cn.edu.xmu.oomall.transaction.util.mq.NotifyMessage;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayTradeState;
import cn.edu.xmu.oomall.transaction.util.alipay.model.vo.AlipayNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatNotifyRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


public class AlipayService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;


    @Transactional()
    public Object paymentNotifyByAlipay(AlipayNotifyVo alipayNotifyVo) {
        Payment payment = new Payment();
        // 已关闭
        if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
            payment.setState(PaymentState.CANCEL.getCode());
        } else if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
            // 交易结束
            // 成功
            payment.setState(PaymentState.ALREADY_PAY.getCode());
        }

        // 创建refundMessage，通过rocketMQ生产者发送
        NotifyMessage refundMessage = createPaymentNotifyMessage(alipayNotifyVo);
        messageProducer.sendNotifyMessage(refundMessage);

        String paymentId = (String) transactionPatternFactory
                .decodeRequestNo(alipayNotifyVo.getOutTradeNo()).get("id");
        payment.setId(Long.parseLong(paymentId));
        payment.setPayTime(alipayNotifyVo.getGmtPayment());
        payment.setTradeSn(alipayNotifyVo.getTradeNo());
        payment.setActualAmount(Long.parseLong(alipayNotifyVo.getBuyerPayAmount()));
        transactionDao.updatePayment(payment);

        return new AlipayNotifyVo();
    }


    @Transactional()
    public Object refundNotifyByAlipay(AlipayNotifyVo alipayNotifyVo) {
        Refund refund = new Refund();
        // 退款失败
        if (alipayNotifyVo.getRefundFee() == null) {
            refund.setState(RefundState.FAILED.getCode());
        } else {
            // 退款成功
            refund.setState(RefundState.FINISH_REFUND.getCode());
        }

        // 创建refundMessage，通过rocketMQ生产者发送
        NotifyMessage refundMessage = createRefundNotifyMessage(alipayNotifyVo);
        messageProducer.sendNotifyMessage(refundMessage);

        String refundId = (String) transactionPatternFactory
                .decodeRequestNo(alipayNotifyVo.getOutBizNo()).get("id");
        refund.setId(Long.parseLong(refundId));
        refund.setRefundTime(alipayNotifyVo.getGmtRefund());
        refund.setTradeSn(alipayNotifyVo.getTradeNo());
        transactionDao.updateRefund(refund);

        return new WechatNotifyRetVo();
    }


    private NotifyMessage createPaymentNotifyMessage(AlipayNotifyVo alipayNotifyVo) {
        NotifyMessage message = new NotifyMessage();
        if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
            message.setState(PaymentState.CANCEL.getCode());
        } else if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
            message.setState(PaymentState.ALREADY_PAY.getCode());
        }

        Map<String, Object> map = transactionPatternFactory.decodeRequestNo(alipayNotifyVo.getOutTradeNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType((Byte) map.get("documentType"));
        message.setMessageType(NotifyMessage.NotifyMessageType.PAYMENT);
        return message;
    }

    private NotifyMessage createRefundNotifyMessage(AlipayNotifyVo alipayNotifyVo) {
        NotifyMessage message = new NotifyMessage();
        if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
            message.setState(RefundState.CANCEL_REFUND.getCode());
        } else if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
            message.setState(RefundState.FINISH_REFUND.getCode());
        }

        Map<String, Object> map = transactionPatternFactory.decodeRequestNo(alipayNotifyVo.getOutTradeNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType((Byte) map.get("documentType"));
        message.setMessageType(NotifyMessage.NotifyMessageType.REFUND);
        return message;
    }
}
