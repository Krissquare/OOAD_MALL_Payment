package cn.edu.xmu.oomall.transaction.util.alipay.service;

import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;

import cn.edu.xmu.oomall.transaction.util.MessageProducer;
import cn.edu.xmu.oomall.transaction.util.NotifyMessage;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.bo.AlipayTradeState;
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



    private NotifyMessage createNotifyMessage(AlipayNotifyVo alipayNotifyVo, NotifyMessage.MessageType messageType) {
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
        message.setMessageType(messageType);
        return message;
    }


    @Transactional()
    public Object paymentNotifyByAlipay(AlipayNotifyVo alipayNotifyVo) {
        Payment payment = new Payment();
        // 已关闭
        if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
            payment.setState(PaymentState.CANCEL.getCode());
        } else if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
            // 交易结束，不能退款
            // 成功
            payment.setState(PaymentState.ALREADY_PAY.getCode());
        }

        // 创建paymentMessage，通过rocketMQ生产者发送
        NotifyMessage paymentMessage = createNotifyMessage(alipayNotifyVo, NotifyMessage.MessageType.PAYMENT);
        messageProducer.sendPaymentMessage(paymentMessage);

        payment.setId(Long.parseLong(alipayNotifyVo.getOutTradeNo()));
        payment.setPayTime(alipayNotifyVo.getGmtPayment());
        payment.setTradeSn(alipayNotifyVo.getTradeNo());
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
        NotifyMessage refundMessage = createNotifyMessage(alipayNotifyVo, NotifyMessage.MessageType.REFUND);
        messageProducer.sendPaymentMessage(refundMessage);

        refund.setId(Long.parseLong(alipayNotifyVo.getOutBizNo()));
        refund.setRefundTime(alipayNotifyVo.getGmtRefund());
        refund.setTradeSn(alipayNotifyVo.getTradeNo());
        transactionDao.updateRefund(refund);

        return new WechatNotifyRetVo();
    }
}
