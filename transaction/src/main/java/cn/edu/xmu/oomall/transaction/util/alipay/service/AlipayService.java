package cn.edu.xmu.oomall.transaction.util.alipay.service;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayRefundState;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayTradeState;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.AlipayNotifyVo;
import cn.edu.xmu.oomall.transaction.util.alipay.model.vo.WechatNotifyRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

public class AlipayService {


    @Autowired
    private TransactionDao transactionDao;

    public final static String ALI_PAY_NOTIFY_KEY="aliPayNotify_%d";

    public final static String ALI_REFUND_NOTIFY_KEY="aliRefundNotify_%d";

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${oomall.transaction.expiretime}")
    private long transactionExpireTime;
    /**
     * gyt
     * 阿里异步t通知API
     * @param alipayNotifyVo
     * @return
     */
    @Transactional(readOnly = true)
    public Object notifyByAlipay(AlipayNotifyVo alipayNotifyVo) {
        //判断是支付还是退款,out_biz_no为空则为支付，不为空则为退款
        //=======支付=======
        if (alipayNotifyVo.getOut_biz_no() == null) {
            //1.根据Paymentid查payment
            ReturnObject returnObject = transactionDao.getPaymentById(Long.parseLong(alipayNotifyVo.getOut_trade_no()));
            if (returnObject.getData() == null) {
                return returnObject;
            }
            Payment payment = (Payment) returnObject.getData();
            //2.判断该通知是否已经处理:在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
            String key=String.format(ALI_PAY_NOTIFY_KEY,Long.parseLong(alipayNotifyVo.getOut_trade_no()));
            //如果为空就set值，并返回true
            //如果存在(不为空)不进行操作，并返回false
            if(!redisTemplate.opsForValue().setIfAbsent(key, alipayNotifyVo, transactionExpireTime, TimeUnit.SECONDS)){
                return new WechatNotifyRetVo();
            }
            //3.修改Payment
            if (alipayNotifyVo.getTrade_status().equals(AlipayTradeState.TRADE_FINISHED.getDescription())) {
                payment.setState(PaymentState.ALREADY_PAY.getCode());
            }
            if (alipayNotifyVo.getTrade_status().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
                payment.setState(PaymentState.CANCEL.getCode());
            }
            if (alipayNotifyVo.getTrade_status().equals(AlipayTradeState.WAIT_BUYER_PAY.getDescription())) {
                payment.setState(PaymentState.WAIT_PAY.getCode());
            }
            if (alipayNotifyVo.getTrade_status().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
                payment.setState(PaymentState.ALREADY_PAY.getCode());
            }
            payment.setTradeSn(alipayNotifyVo.getTrade_no());
            transactionDao.updatePayment(payment);
            return new WechatNotifyRetVo();
            //TODO:4.rocketMQ 通知订单改状态
        } else {
            //1.根据refundid查refund
            ReturnObject returnObject = transactionDao.getRefundById(Long.parseLong(alipayNotifyVo.getOut_biz_no()));
            if (returnObject.getData() == null) {
                return returnObject;
            }
            Refund refund = (Refund) returnObject.getData();
            //2.判断该通知是否已经处理:在对业务数据进行状态检查和处理之前，要采用数据锁进行并发控制，以避免函数重入造成的数据混乱
            String key=String.format(ALI_REFUND_NOTIFY_KEY,Long.parseLong(alipayNotifyVo.getOut_biz_no()));
            //如果为空就set值，并返回true
            //如果存在(不为空)不进行操作，并返回false
            if(!redisTemplate.opsForValue().setIfAbsent(key, alipayNotifyVo,transactionExpireTime, TimeUnit.SECONDS)){
                return new WechatNotifyRetVo();
            }
            //3.修改refund状态
            if (alipayNotifyVo.getTrade_status().equals(AlipayRefundState.REFUND_SUCCESS.getDescription())) {
                refund.setState(RefundState.FINISH_REFUND.getCode());
            }
            refund.setTradeSn(alipayNotifyVo.getTrade_no());
            transactionDao.updateRefund(refund);
            return new WechatNotifyRetVo();
            //TODO:4.rocketMQ 通知订单改状态
        }
    }


}
