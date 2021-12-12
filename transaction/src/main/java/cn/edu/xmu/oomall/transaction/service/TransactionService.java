package cn.edu.xmu.oomall.transaction.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.Payment;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentState;
import cn.edu.xmu.oomall.transaction.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.xmu.oomall.transaction.model.bo.Refund;
import cn.edu.xmu.oomall.transaction.model.bo.RefundState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

@Service
public class TransactionService {
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private AlipayService alipayService;
    @Autowired
    private WeChatPayService weChatPayService;
    public final static String WECHAT_PAY_NOTIFY_KEY="weChatPayNotify_%d";
    public final static String WECHAT_REFUND_NOTIFY_KEY="weChatRefundNotify_%d";
    public final static String ALI_PAY_NOTIFY_KEY="aliPayNotify_%d";
    public final static String ALI_REFUND_NOTIFY_KEY="aliRefundNotify_%d";
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${oomall.transaction.expiretime}")
    private long transactionExpireTime;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;

    /**
     * gyt
     * 平台管理员查询支付信息
     *
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject listPayment(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        return transactionDao.listPayment(null, documentId, state, beginTime, endTime, page, pageSize);

    }

    /**
     * gyt
     * 平台管理员查询支付信息详情
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getPaymentDetails(Long id) {
        ReturnObject returnObject = transactionDao.getPaymentById(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        PaymentDetailRetVo paymentDetailRetVo = cloneVo(returnObject.getData(), PaymentDetailRetVo.class);
        return new ReturnObject(paymentDetailRetVo);
    }

    /**
     * gyt
     * 平台管理员修改支付信息
     *
     * @param id
     * @param loginUserId
     * @param loginUserName
     * @param paymentModifyVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updatePayment(Long id, Long loginUserId, String loginUserName, PaymentModifyVo paymentModifyVo) {
        ReturnObject returnObject = transactionDao.getPaymentById(id);
        if (!returnObject.getCode().equals(ReturnNo.OK)) {
            return returnObject;
        }
        Payment payment = (Payment) returnObject.getData();
        if (payment.getState().equals(PaymentState.ALREADY_PAY.getCode()) || payment.getState().equals(PaymentState.FAIL.getCode())) {
            Payment payment1 = cloneVo(paymentModifyVo, Payment.class);
            payment1.setId(id);
            setPoModifiedFields(payment1, loginUserId, loginUserName);
            ReturnObject returnObject1 = transactionDao.updatePayment(payment1);
            if (!returnObject1.getCode().equals(ReturnNo.OK)) {
                return returnObject1;
            }
            PaymentDetailRetVo paymentDetailRetVo = cloneVo(returnObject1.getData(), PaymentDetailRetVo.class);
            return new ReturnObject(paymentDetailRetVo);
        } else {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
    }

    /**
     * hty
     * 平台管理员查询退款
     *
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject listRefund(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        return transactionDao.listRefund(documentId, state, null, beginTime, endTime, page, pageSize);
    }

    /**
     * hty
     * 获取退款详情
     *
     * @param id
     * @return
     */

    public ReturnObject getRefundDetail(Long id) {
        ReturnObject ret = transactionDao.getRefundById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        RefundDetailVo returnDetailVo = cloneVo(ret.getData(), RefundDetailVo.class);
        return new ReturnObject(returnDetailVo);
    }

    /**
     * hty
     * 修改退款信息
     *
     * @param id
     * @param refundRecVo
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    public ReturnObject updateRefund(Long id, RefundRecVo refundRecVo, Long loginUserId, String loginUserName) {
        ReturnObject ret = transactionDao.getRefundById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Refund refund1 = (Refund) ret.getData();
        if (refund1.getState() != RefundState.FINISH_REFUND.getCode()) {
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        refund1.setState(refundRecVo.getState());
        refund1.setDescr(refundRecVo.getDescr());
        setPoModifiedFields(refund1, loginUserId, loginUserName);
        refund1.setAdjustId(loginUserId);
        refund1.setAdjustName(loginUserName);
        ReturnObject returnObject = transactionDao.updateRefund(refund1);
        if (!returnObject.getCode().equals(ReturnNo.OK.getCode())) {
            return returnObject;
        }
        return new ReturnObject(cloneVo(refund1, RefundDetailVo.class));
    }

    /**
     * gyt
     * 微信支付通知API
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
        String key=String.format(WECHAT_PAY_NOTIFY_KEY,id);
        //如果为空就set值，并返回true
        //如果存在(不为空)不进行操作，并返回false
        if(!redisTemplate.opsForValue().setIfAbsent(key, wechatPaymentNotifyVo,transactionExpireTime, TimeUnit.SECONDS)){
            return new NotifyRetVo();
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
        return new NotifyRetVo();
        //TODO:4.rocketMQ 通知订单改状态
    }
    /**
     * gyt
     * 微信退款通知API
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
        String key=String.format(WECHAT_REFUND_NOTIFY_KEY,id);
        //如果为空就set值，并返回true
        //如果存在(不为空)不进行操作，并返回false
        if(!redisTemplate.opsForValue().setIfAbsent(key, wechatRefundNotifyVo,transactionExpireTime, TimeUnit.SECONDS)){
            return new NotifyRetVo();
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
        return new NotifyRetVo();
        //TODO:4.rocketMQ 通知订单改状态
    }
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
            if(!redisTemplate.opsForValue().setIfAbsent(key, alipayNotifyVo,transactionExpireTime, TimeUnit.SECONDS)){
                return new NotifyRetVo();
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
            return new NotifyRetVo();
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
                return new NotifyRetVo();
            }
            //3.修改refund状态
            if (alipayNotifyVo.getTrade_status().equals(AlipayRefundState.REFUND_SUCCESS.getDescription())) {
                refund.setState(RefundState.FINISH_REFUND.getCode());
            }
            refund.setTradeSn(alipayNotifyVo.getTrade_no());
            transactionDao.updateRefund(refund);
            return new NotifyRetVo();
            //TODO:4.rocketMQ 通知订单改状态
        }
    }

    /**
     * 内部API退款
     * @param refundVo
     * @return
     */
    public Object refund(RefundVo refundVo) {
        //1.查payment 检查
        ReturnObject returnObject = transactionDao.getPaymentById(refundVo.getPaymentId());
        if (returnObject.getData() == null) {
            return returnObject;
        }
        Payment payment = (Payment) returnObject.getData();
        if (!((refundVo.getPatternId().equals(payment.getPatternId())) && (refundVo.getDocumentId().equals(payment.getDocumentId())))) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);

        }
        //2.将refund写进数据库
        Refund refund = cloneVo(refundVo, Refund.class);
        //TODO:要setcreator吗？
        ReturnObject returnObject1 = transactionDao.insertRefund(refund);
        if (returnObject1.getData() == null) {
            return returnObject1;
        }
        Refund refundRet = (Refund) returnObject1.getData();
        //3.根据pattern调支付宝或微信的接口
        //支付宝
        if (refundRet.getPatternId() == 0) {
            AlipayRefundVo alipayRefundVo = new AlipayRefundVo();
            alipayRefundVo.setRefundAmount(refundVo.getAmount());
            alipayRefundVo.setOutTradeNo(refundVo.getPaymentId().toString());
            alipayRefundVo.setOutRequestNo(refundRet.getId().toString());
            //TODO:将vo转为json
            String biz_content = "vo转json";
            alipayService.gatewayDo(null, AlipayMethod.REFUND.getMethod(), null, null, null, null, null, null,"vo转json");
        }
        //微信支付
        if (refundRet.getPatternId() == 1) {
            WeChatPayRefundVo weChatPayRefundVo = new WeChatPayRefundVo();
            weChatPayRefundVo.setOutRefundNo(refundRet.getId().toString());
            weChatPayRefundVo.setOutTradeNo(refundVo.getPaymentId().toString());
            RefundAmountVo refundAmountVo = new RefundAmountVo();
            refundAmountVo.setRefund(Integer.parseInt(String.valueOf(refundVo.getAmount())));
            refundAmountVo.setTotal(Integer.parseInt(String.valueOf(payment.getAmount())));
            weChatPayRefundVo.setAmount(refundAmountVo);
            weChatPayService.createRefund(weChatPayRefundVo);
        }
        return new InternalReturnObject();
    public ReturnObject paymentNotifyByWechat(WechatPaymentNotifyVo wechatPaymentNotifyVo) {

        return null;
    }


    public ReturnObject requestPayment(PaymentBill paymentBill, Long loginUserId, String loginUserName) {
        // 根据documentId, documentType去查payment
        ReturnObject<PageInfo<Payment>> retPaymentPageInfo =
                transactionDao.listPayment(paymentBill.getPatternId(), null, paymentBill.getDocumentType(), null, null, null, 1, 100);
        if (!retPaymentPageInfo.getCode().equals(ReturnNo.OK)) {
            return retPaymentPageInfo;
        }

        // 获取paymentList
        Map<String, Object> retMap = (Map<String, Object>) retPaymentPageInfo.getData();
        List<Payment> paymentList = (List<Payment>) retMap.get("list");

        Payment validExistedPayment = null;
        for (Payment payment : paymentList) {
            // 判断是否存在已支付、已对账、已清算的流水
            if (payment.getState().equals(PaymentState.ALREADY_PAY.getCode()) ||
                payment.getState().equals(PaymentState.ALREADY_RECONCILIATION.getCode()) ||
                payment.getState().equals(PaymentState.ALREADY_LIQUIDATION.getCode())) {
                return new ReturnObject(ReturnNo.STATENOTALLOW);
            }

            // TODO: 判断是否在beginTime和endTime内

            // 判断是否存在待支付超时流水，不在这里判断
//            if (LocalDateTime.now().isAfter(payment.getEndTime()) &&
//                payment.getState().equals(PaymentState.WAIT_PAY.getCode())) {
//                payment.setState(PaymentState.CANCLE.getCode());
//                setPoModifiedFields(payment, loginUserId, loginUserName);
//
//
//                transactionDao.updatePayment(payment);
//            }

            // 判断是否存在匹配支付渠道的待支付流水
            if (paymentBill.getPatternId().equals(payment.getPatternId()) &&
                payment.getState().equals(PaymentState.WAIT_PAY.getCode())) {
                validExistedPayment = payment;
            }
        }

        // 开始请求支付
        TransactionPattern pattern = transactionPatternFactory.getPatternInstance(paymentBill.getPatternId());
        if (validExistedPayment == null) {
            // 不存在匹配的流水，则需要新建
            Payment payment = cloneVo(paymentBill, Payment.class);
            setPoCreatedFields(payment, loginUserId, loginUserName);
            setPoModifiedFields(payment, loginUserId, loginUserName);
            ReturnObject<Payment> retPayment = transactionDao.insertPayment(payment);

            if (!retPayment.getCode().equals(ReturnNo.OK)) {
                return retPayment;
            }

            // 然后请求支付宝
            ReturnObject ret = pattern.requestPayment(retPayment.getData().getId(), paymentBill);


        } else {
            // 存在匹配的流水，则需要更新
        }

        return null;
    }

}
