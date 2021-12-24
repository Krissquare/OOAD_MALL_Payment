package cn.edu.xmu.oomall.transaction.util.wechatpay.service;

import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;
import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.mq.PaymentNotifyMessage;
import cn.edu.xmu.oomall.transaction.util.mq.RefundNotifyMessage;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatNotifyRetVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatPaymentNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatRefundNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatRefundState;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatTradeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
public class WechatService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private MessageProducer messageProducer;


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
        // 创建paymentNotifyMessage，通过rocketMQ生产者发送
        PaymentNotifyMessage message = new PaymentNotifyMessage();

        if (notifyState.equals(WechatTradeState.CLOSED.getState())) {
            // 已关闭

            // 根据请求号解码出Id，更新数据库
            String paymentId = (String) TransactionPatternFactory
                    .decodeRequestNo(wechatPaymentNotifyVo.getResource().getCiphertext().getOutTradeNo()).get("id");
            payment.setId(Long.parseLong(paymentId));
            payment.setState(PaymentState.FAIL.getCode());
            transactionDao.updatePayment(payment);

            message.setPaymentState(PaymentState.FAIL);
        } else if (notifyState.equals(WechatTradeState.SUCCESS.getState())) {
            // 交易成功

            // 根据请求号解码出Id，更新数据库
            String paymentId = (String) TransactionPatternFactory
                    .decodeRequestNo(wechatPaymentNotifyVo.getResource().getCiphertext().getOutTradeNo()).get("id");
            payment.setId(Long.parseLong(paymentId));
            payment.setState(PaymentState.ALREADY_PAY.getCode());
            payment.setPayTime(wechatPaymentNotifyVo.getResource().getCiphertext().getSuccessTime());
            payment.setTradeSn(wechatPaymentNotifyVo.getResource().getCiphertext().getTransactionId());
            payment.setActualAmount(wechatPaymentNotifyVo.getResource().getCiphertext().getAmount().getPayerTotal().longValue());
            transactionDao.updatePayment(payment);

            message.setPaymentState(PaymentState.ALREADY_PAY);
        }

        // 通知其他模块支付情况
        Map<String, Object> map = TransactionPatternFactory.decodeRequestNo(wechatPaymentNotifyVo.getResource().getCiphertext().getOutTradeNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType(Byte.parseByte((String) map.get("documentType")));
        messageProducer.sendPaymentNotifyMessage(message);


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
        // 创建refundMessage，通过rocketMQ生产者发送
        RefundNotifyMessage message = new RefundNotifyMessage();
        if (wechatRefundNotifyVo.getResource().getCiphertext().getRefundStatus().equals(WechatRefundState.SUCCESS.getState())) {
            refund.setState(RefundState.FINISH_REFUND.getCode());
            message.setRefundState(RefundState.FINISH_REFUND);
        } else {
            refund.setState(RefundState.FAILED.getCode());
            message.setRefundState(RefundState.FAILED);
        }

        // 根据请求号解码出Id，更新数据库
        String refundId =(String) TransactionPatternFactory
                .decodeRequestNo(wechatRefundNotifyVo.getResource().getCiphertext().getOutRefundNo()).get("id");
        refund.setId(Long.parseLong(refundId));
        refund.setRefundTime(wechatRefundNotifyVo.getResource().getCiphertext().getSuccessTime());
        refund.setTradeSn(wechatRefundNotifyVo.getResource().getCiphertext().getRefundId());
        transactionDao.updateRefund(refund);

        // 通知其他模块退款情况
        Map<String, Object> map = TransactionPatternFactory.decodeRequestNo(wechatRefundNotifyVo.getResource().getCiphertext().getOutRefundNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType(Byte.parseByte((String) map.get("documentType")));
        messageProducer.sendRefundNotifyMessage(message);

        return new WechatNotifyRetVo();
    }

}
