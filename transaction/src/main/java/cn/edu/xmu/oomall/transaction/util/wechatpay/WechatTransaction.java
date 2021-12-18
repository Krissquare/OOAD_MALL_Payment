package cn.edu.xmu.oomall.transaction.util.wechatpay;


import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;
import cn.edu.xmu.oomall.transaction.model.vo.ReconciliationRetVo;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.file.FileUtil;
import cn.edu.xmu.oomall.transaction.util.file.bo.WechatTypeState;
import cn.edu.xmu.oomall.transaction.util.file.vo.AliPayFormat;
import cn.edu.xmu.oomall.transaction.util.file.vo.WechatFormat;
import cn.edu.xmu.oomall.transaction.util.mq.ActiveQueryMessage;
import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.WechatMicroService;
import cn.edu.xmu.oomall.transaction.util.wechatpay.microservice.vo.*;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatRefundState;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatReturnObject;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo.WechatTradeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;


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
           queryMessage.setBill(bill);
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
    @Override
    public void closeTransaction(String requestNo){
        wechatMicroService.closeTransaction(requestNo);
    }
    @Override
    public String getFundFlowBill(String billDate){
        WechatReturnObject<WeChatPayFundFlowBillRetVo> wechatReturnObject=wechatMicroService.getFundFlowBill(billDate);
        WeChatPayFundFlowBillRetVo weChatPayFundFlowBillRetVo=wechatReturnObject.getData();
        return weChatPayFundFlowBillRetVo.getDownloadUrl();
    }
    @Override
    public ReturnObject reconciliation(LocalDateTime beginTime,LocalDateTime endTime){
        Integer success=0;
        Integer error=0;
        Integer extra=0;
        //1.提取微信支付流水
        String url=getFundFlowBill("这个没用");
        //TODO:拿到下载地址以后下载，得到zip
        FileUtil.unZip(new File("testfile/wechat/微信支付账单(20211011-20211211).zip"), "testfile/wechat");
        List<WechatFormat> list = FileUtil.wechatParsing(new File("testfile/wechat/微信支付账单(20211011-20211211)/微信支付账单(20211011-20211211).csv"));
        //2.遍历支付宝流水，进行对账
        for(WechatFormat wechatFormat:list){
            //时间不符
            if(!(wechatFormat.getTradeCreateTime().isAfter(beginTime)&&wechatFormat.getTradeCreateTime().isBefore(endTime))){
                break;
            }
            //平台收入，对应Payment
            if(wechatFormat.getType().equals(WechatTypeState.REFUND)){
                ReturnObject returnObject=transactionDao.getPaymentByTradeSn(wechatFormat.getTradeNo());
                if(!returnObject.getCode().equals(ReturnNo.OK)){
                    return returnObject;
                }
                //商城没有：长账,插入错误账
                if(returnObject.getData()==null){
                    ErrorAccount errorAccount=new ErrorAccount();
                    errorAccount.setTradeSn(wechatFormat.getTradeNo());
                    errorAccount.setPatternId(2L);
                    errorAccount.setIncome(wechatFormat.getAmount());
                    errorAccount.setExpenditure(0L);
                    errorAccount.setState((byte)0);
                    errorAccount.setTime(wechatFormat.getTradeCreateTime());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                }
                else{
                    Payment payment=(Payment)returnObject.getData();
                    //错账，插入错误账
                    if(!payment.getActualAmount().equals(wechatFormat.getAmount())){
                        ErrorAccount errorAccount=new ErrorAccount();
                        errorAccount.setTradeSn(wechatFormat.getTradeNo());
                        errorAccount.setPatternId(2L);
                        errorAccount.setIncome(wechatFormat.getAmount());
                        errorAccount.setExpenditure(0L);
                        errorAccount.setState((byte)0);
                        errorAccount.setTime(wechatFormat.getTradeCreateTime());
                        ReturnObject returnObject1=transactionDao.insertErrorAccount(errorAccount);
                        if(!returnObject1.getCode().equals(ReturnNo.OK.getCode()))
                        {
                            return returnObject1;
                        }
                        error++;
                    }
                    //对账成功，更改状态
                    else {
                        payment.setState(PaymentState.ALREADY_RECONCILIATION.getCode());
                        ReturnObject returnObject1=transactionDao.updatePayment(payment);
                        if(!returnObject1.getCode().equals(ReturnNo.OK.getCode()))
                        {
                            return returnObject1;
                        }
                        success++;
                    }

                }
            }
            //平台支出，对应refund
            else{
                ReturnObject returnObject=transactionDao.getRefundByTradeSn(wechatFormat.getTradeNo());
                if(!returnObject.getCode().equals(ReturnNo.OK)){
                    return returnObject;
                }
                //商城没有：长账，插入错误账
                if(returnObject.getData()==null){
                    ErrorAccount errorAccount=new ErrorAccount();
                    errorAccount.setTradeSn(wechatFormat.getTradeNo());
                    errorAccount.setPatternId(2L);
                    errorAccount.setIncome(0L);
                    errorAccount.setExpenditure(wechatFormat.getAmount());
                    errorAccount.setState((byte)0);
                    errorAccount.setTime(wechatFormat.getTradeCreateTime());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                }
                else{
                    Refund refund=(Refund)returnObject.getData();
                    //错账，插入错误账
                    if(!refund.getAmount().equals(wechatFormat.getAmount())){
                        ErrorAccount errorAccount=new ErrorAccount();
                        errorAccount.setTradeSn(wechatFormat.getTradeNo());
                        errorAccount.setPatternId(2L);
                        errorAccount.setIncome(0L);
                        errorAccount.setExpenditure(wechatFormat.getAmount());
                        errorAccount.setState((byte)0);
                        errorAccount.setTime(wechatFormat.getTradeCreateTime());
                        ReturnObject returnObject1=transactionDao.insertErrorAccount(errorAccount);
                        if(!returnObject1.getCode().equals(ReturnNo.OK.getCode()))
                        {
                            return returnObject1;
                        }
                        error++;
                    }
                    //对账成功，更改状态
                    else {
                        refund.setState(RefundState.FINISH_RECONCILIATION.getCode());
                        ReturnObject returnObject1=transactionDao.updateRefund(refund);
                        if(!returnObject1.getCode().equals(ReturnNo.OK.getCode()))
                        {
                            return returnObject1;
                        }
                        success++;
                    }

                }

            }

        }
        ReconciliationRetVo reconciliationRetVo=new ReconciliationRetVo();
        reconciliationRetVo.setError(error);
        reconciliationRetVo.setSuccess(success);
        reconciliationRetVo.setExtra(extra);
        return new ReturnObject(reconciliationRetVo);

    }
}