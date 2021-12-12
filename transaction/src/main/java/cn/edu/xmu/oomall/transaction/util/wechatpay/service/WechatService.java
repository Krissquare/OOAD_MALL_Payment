package cn.edu.xmu.oomall.transaction.util.wechatpay.service;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatNotifyRetVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatPaymentNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatRefundNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatRefundState;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatTradeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

public class WechatService {

    @Autowired
    private TransactionDao transactionDao;


    public final static String WECHAT_PAY_NOTIFY_KEY = "weChatPayNotify_%d";

    public final static String WECHAT_REFUND_NOTIFY_KEY = "weChatRefundNotify_%d";

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${oomall.transaction.expiretime}")
    private long transactionExpireTime;


    /**
     * gyt
     * 微信支付通知API
     *
     * @param wechatPaymentNotifyVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Object paymentNotifyByWechat(WechatPaymentNotifyVo wechatPaymentNotifyVo) {
        //1.根据Paymentid查payment
        Long id = Long.parseLong(wechatPaymentNotifyVo.getResource().getCiphertext().getOut_trade_no());
        ReturnObject returnObject = transactionDao.getPaymentById(id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        Payment payment = (Payment) returnObject.getData();
        //2.判断该通知是否已经处理:在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
        String key = String.format(WECHAT_PAY_NOTIFY_KEY, id);
        //如果为空就set值，并返回true
        //如果存在(不为空)不进行操作，并返回false
        if (!redisTemplate.opsForValue().setIfAbsent(key, wechatPaymentNotifyVo, transactionExpireTime, TimeUnit.SECONDS)) {
            return new WechatNotifyRetVo();
        }
        //3.修改Payment
        String notifyState = wechatPaymentNotifyVo.getResource().getCiphertext().getTrade_state();
        if (notifyState.equals(WechatTradeState.SUCCESS.getState())) {
            payment.setState(PaymentState.ALREADY_PAY.getCode());
        }
        if (notifyState.equals(WechatTradeState.CLOSED.getState())) {
            payment.setState(PaymentState.CANCEL.getCode());
        }
        if (notifyState.equals(WechatTradeState.REFUND.getState())) {
            payment.setState(PaymentState.ALREADY_PAY.getCode());
        }
        payment.setTradeSn(wechatPaymentNotifyVo.getResource().getCiphertext().getTransaction_id());
        transactionDao.updatePayment(payment);
        return new WechatNotifyRetVo();
        //TODO:4.rocketMQ 通知订单改状态
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
        //1.根据refundid查refund
        Long id = Long.parseLong(wechatRefundNotifyVo.getResource().getCiphertext().getOut_refund_no());
        ReturnObject returnObject = transactionDao.getRefundById(id);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        Refund refund = (Refund) returnObject.getData();
        //2.判断该通知是否已经处理:在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
        String key = String.format(WECHAT_REFUND_NOTIFY_KEY, id);
        //如果为空就set值，并返回true
        //如果存在(不为空)不进行操作，并返回false
        if (!redisTemplate.opsForValue().setIfAbsent(key, wechatRefundNotifyVo, transactionExpireTime, TimeUnit.SECONDS)) {
            return new WechatNotifyRetVo();
        }
        //3.修改Prefund状态
        if (wechatRefundNotifyVo.getResource().getCiphertext().getRefund_status().equals(WechatRefundState.SUCCESS.getState())) {
            refund.setState(RefundState.FINISH_REFUND.getCode());
        }
        if (wechatRefundNotifyVo.getResource().getCiphertext().getRefund_status().equals(WechatRefundState.ABNORMAL.getState())) {
            refund.setState(RefundState.FAILED.getCode());
        }
        refund.setTradeSn(wechatRefundNotifyVo.getResource().getCiphertext().getTransaction_id());
        transactionDao.updateRefund(refund);
        return new WechatNotifyRetVo();
        //TODO:4.rocketMQ 通知订单改状态
    }

}
