package cn.edu.xmu.oomall.transaction.util.alipay.service;

import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;

import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayTradeState;
import cn.edu.xmu.oomall.transaction.util.alipay.model.vo.AlipayNotifyVo;
import cn.edu.xmu.oomall.transaction.util.mq.PaymentNotifyMessage;
import cn.edu.xmu.oomall.transaction.util.mq.RefundNotifyMessage;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatNotifyRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AlipayService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private MessageProducer messageProducer;

    @Transactional()
    public Object paymentNotifyByAlipay(AlipayNotifyVo alipayNotifyVo) {
        Payment payment = new Payment();
        // 创建paymentNotifyMessage，通过rocketMQ生产者发送
        PaymentNotifyMessage message = new PaymentNotifyMessage();
        if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
            // 已关闭

            // 根据请求号解码出Id，更新数据库
            String paymentId = (String) TransactionPatternFactory
                    .decodeRequestNo(alipayNotifyVo.getOutTradeNo()).get("id");
            payment.setId(Long.parseLong(paymentId));
            payment.setState(PaymentState.FAIL.getCode());
            transactionDao.updatePayment(payment);

            message.setPaymentState(PaymentState.FAIL);
        } else if (alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                alipayNotifyVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
            // 交易结束
            // 成功

            // 根据请求号解码出Id，更新数据库
            String paymentId = (String) TransactionPatternFactory
                    .decodeRequestNo(alipayNotifyVo.getOutTradeNo()).get("id");
            payment.setId(Long.parseLong(paymentId));
            payment.setState(PaymentState.ALREADY_PAY.getCode());
            payment.setPayTime(alipayNotifyVo.getGmtPayment());
            payment.setTradeSn(alipayNotifyVo.getTradeNo());
            payment.setActualAmount(Long.parseLong(alipayNotifyVo.getBuyerPayAmount()));
            transactionDao.updatePayment(payment);

            message.setPaymentState(PaymentState.ALREADY_PAY);
        }

        // 通知其他模块支付情况
        Map<String, Object> map = TransactionPatternFactory.decodeRequestNo(alipayNotifyVo.getOutTradeNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType(Byte.parseByte((String) map.get("documentType")));
        messageProducer.sendPaymentNotifyMessage(message);


        return new AlipayNotifyVo();
    }


    @Transactional()
    public Object refundNotifyByAlipay(AlipayNotifyVo alipayNotifyVo) {
        Refund refund = new Refund();
        // 创建refundMessage，通过rocketMQ生产者发送
        RefundNotifyMessage message = new RefundNotifyMessage();
        if (alipayNotifyVo.getRefundFee() == null) {
            // 退款失败
            refund.setState(RefundState.FAILED.getCode());
            message.setRefundState(RefundState.FAILED);
        } else {
            // 退款成功
            refund.setState(RefundState.FINISH_REFUND.getCode());
            message.setRefundState(RefundState.FINISH_REFUND);
        }

        // 根据请求号解码出Id，更新数据库
        String refundId = (String) TransactionPatternFactory
                .decodeRequestNo(alipayNotifyVo.getOutBizNo()).get("id");
        refund.setId(Long.parseLong(refundId));
        refund.setRefundTime(alipayNotifyVo.getGmtRefund());
        refund.setTradeSn(alipayNotifyVo.getTradeNo());
        transactionDao.updateRefund(refund);

        // 通知其他模块退款情况
        Map<String, Object> map = TransactionPatternFactory.decodeRequestNo(alipayNotifyVo.getOutBizNo());
        message.setDocumentId((String) map.get("documentId"));
        message.setDocumentType(Byte.parseByte((String) map.get("documentType")));
        messageProducer.sendRefundNotifyMessage(message);

        return new WechatNotifyRetVo();
    }
}
