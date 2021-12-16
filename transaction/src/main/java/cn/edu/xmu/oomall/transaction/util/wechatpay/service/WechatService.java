package cn.edu.xmu.oomall.transaction.util.wechatpay.service;

import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;
import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import cn.edu.xmu.oomall.transaction.util.mq.NotifyMessage;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatNotifyRetVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatPaymentNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatRefundNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatRefundState;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatTradeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


public class WechatService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    /**
     * gyt
     * 微信支付通知API
     *
     * @param wechatPaymentNotifyVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Object paymentNotifyByWechat(WechatPaymentNotifyVo wechatPaymentNotifyVo) {
        Payment payment = new Payment();
        String notifyState = wechatPaymentNotifyVo.getResource().getCiphertext().getTradeState();
        // 已关闭
        if (notifyState.equals(WechatTradeState.CLOSED.getState())) {
            payment.setState(PaymentState.CANCEL.getCode());
        } else if (notifyState.equals(WechatTradeState.SUCCESS.getState())) {
            // 交易成功
            payment.setState(PaymentState.ALREADY_PAY.getCode());
        }

        // 创建paymentMessage，通过rocketMQ生产者发送
        NotifyMessage paymentMessage = createPaymentNotifyMessage(wechatPaymentNotifyVo);
        messageProducer.sendNotifyMessage(paymentMessage);

        String paymentId = (String) transactionPatternFactory
                .decodeRequestNo(wechatPaymentNotifyVo.getResource().getCiphertext().getOutTradeNo())
                .get("id");
        payment.setId(Long.parseLong(paymentId));
        payment.setPayTime(wechatPaymentNotifyVo.getResource().getCiphertext().getSuccessTime());
        payment.setTradeSn(wechatPaymentNotifyVo.getResource().getCiphertext().getTransactionId());
        payment.setActualAmount(wechatPaymentNotifyVo.getResource().getCiphertext().getAmount().getPayerTotal().longValue());
        transactionDao.updatePayment(payment);

        return new WechatNotifyRetVo();

    }
    /**
     * gyt
     * 微信退款通知API
     *
     * @param wechatRefundNotifyVo
     * @return
     */
    @Transactional(readOnly = true)
    public Object refundNotifyByWechat(WechatRefundNotifyVo wechatRefundNotifyVo) {
        Refund refund = new Refund();
        if (wechatRefundNotifyVo.getResource().getCiphertext().getRefundStatus().equals(WechatRefundState.SUCCESS.getState())) {
            refund.setState(RefundState.FAILED.getCode());
        } else {
            refund.setState(RefundState.FINISH_REFUND.getCode());
        }

        // 创建refundMessage，通过rocketMQ生产者发送
        NotifyMessage refundMessage = createRefundNotifyMessage(wechatRefundNotifyVo);
        messageProducer.sendNotifyMessage(refundMessage);

        String refundId =(String) transactionPatternFactory
                .decodeRequestNo(wechatRefundNotifyVo.getResource().getCiphertext().getOutRefundNo())
                .get("id");
        refund.setId(Long.parseLong(refundId));
        refund.setRefundTime(wechatRefundNotifyVo.getResource().getCiphertext().getSuccessTime());
        refund.setTradeSn(wechatRefundNotifyVo.getResource().getCiphertext().getRefundId());
        transactionDao.updateRefund(refund);

        return new WechatNotifyRetVo();
    }


    private NotifyMessage createPaymentNotifyMessage(WechatPaymentNotifyVo wechatPaymentNotifyVo) {
        NotifyMessage message = new NotifyMessage();
        String notifyState = wechatPaymentNotifyVo.getResource().getCiphertext().getTradeState();
        if (notifyState.equals(WechatTradeState.CLOSED.getState())) {
            message.setState(PaymentState.CANCEL.getCode());
        } else if (notifyState.equals(WechatTradeState.SUCCESS.getState())) {
            message.setState(PaymentState.ALREADY_PAY.getCode());
        }

        Map<String, Object> map = transactionPatternFactory
                .decodeRequestNo(wechatPaymentNotifyVo.getResource().getCiphertext().getOutTradeNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType((Byte) map.get("documentType"));
        message.setMessageType(NotifyMessage.NotifyMessageType.PAYMENT);
        return message;
    }


    private NotifyMessage createRefundNotifyMessage(WechatRefundNotifyVo wechatRefundNotifyVo) {
        NotifyMessage message = new NotifyMessage();
        String notifyState = wechatRefundNotifyVo.getResource().getCiphertext().getRefundStatus();
        if (notifyState.equals(WechatRefundState.ABNORMAL.getState())) {
            message.setState(RefundState.CANCEL_REFUND.getCode());
        } else if (notifyState.equals(WechatTradeState.SUCCESS.getState())) {
            message.setState(RefundState.FINISH_REFUND.getCode());
        }

        Map<String, Object> map = transactionPatternFactory
                .decodeRequestNo(wechatRefundNotifyVo.getResource().getCiphertext().getOutRefundNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType((Byte) map.get("documentType"));
        message.setMessageType(NotifyMessage.NotifyMessageType.REFUND);
        return message;
    }
}
