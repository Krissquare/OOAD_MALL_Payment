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
import cn.edu.xmu.oomall.transaction.util.billformatter.FileUtil;
import cn.edu.xmu.oomall.transaction.util.billformatter.bo.WechatTypeState;
import cn.edu.xmu.oomall.transaction.util.billformatter.vo.WechatFormat;
import cn.edu.xmu.oomall.transaction.util.mq.*;
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
import java.util.Map;


@Component
public class WechatTransaction extends TransactionPattern {

    @Autowired
    private WechatMicroService wechatMicroService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private TransactionDao transactionDao;


    @Override
    public void requestPayment(PaymentBill bill) {
        WechatPaymentVo paymentVo = new WechatPaymentVo();
        paymentVo.setOutTradeNo(bill.getOutTradeNo());
        paymentVo.getAmount().setTotal(bill.getAmount());

        WechatReturnObject<WechatPaymentRetVo> wechatReturnObject = wechatMicroService.requestPayment(paymentVo);

        // prepayId????????????
        // ???????????????????????????????????????
        PaymentQueryMessage paymentQueryMessage = new PaymentQueryMessage();
        paymentQueryMessage.setPaymentBill(bill);
        messageProducer.sendPaymentQueryDelayedMessage(paymentQueryMessage);
    }

    @Override
    public void requestRefund(RefundBill bill) {
        WechatRefundVo refundVo = new WechatRefundVo();
        WechatRefundVo.RefundAmountVo amountVo = new WechatRefundVo.RefundAmountVo();
        refundVo.setOutRefundNo(bill.getOutRefundNo());
        refundVo.setOutTradeNo(bill.getOutTradeNo());
        refundVo.setReason(bill.getReason());
        amountVo.setRefund(bill.getAmount());
        refundVo.setAmount(amountVo);

       WechatReturnObject<WechatRefundRetVo> ret = wechatMicroService.requestRefund(refundVo);

       if (ret != null) {
           WechatRefundRetVo wechatRefundRetVo = ret.getData();
           Refund refund = new Refund();
           // ??????refundMessage?????????rocketMQ???????????????
           RefundNotifyMessage message = new RefundNotifyMessage();
           if (wechatRefundRetVo.getStatus().equals(WechatRefundState.SUCCESS.getState())) {
               refund.setState(RefundState.FINISH_REFUND.getCode());
               message.setRefundState(RefundState.FINISH_REFUND);
           } else {
               refund.setState(RefundState.FAILED.getCode());
               message.setRefundState(RefundState.FAILED);
           }
           // ???????????????
           Long refundId = bill.getRelatedRefund().getId();
           refund.setId(refundId);
           refund.setRefundTime(wechatRefundRetVo.getSuccessTime());
           refund.setTradeSn(wechatRefundRetVo.getTransactionId());
           transactionDao.updateRefund(refund);

           // ??????????????????????????????
           Map<String, Object> map = TransactionPatternFactory.decodeRequestNo(bill.getOutRefundNo());
           message.setDocumentId((String) map.get("documentId"));
           message.setDocumentType(Byte.parseByte((String) map.get("documentType")));
           messageProducer.sendRefundNotifyMessage(message);
       }
        // ?????????????????????????????????????????????????????????????????????????????????
        RefundQueryMessage refundQueryMessage = new RefundQueryMessage();
        refundQueryMessage.setRefundBill(bill);
        messageProducer.sendRefundQueryDelayedMessage(refundQueryMessage);
    }

    @Override
    public void queryPayment(PaymentBill bill) {
        Long paymentId = bill.getRelatedPayment().getId();
        ReturnObject<Payment> retPayment = transactionDao.getPaymentById(paymentId);
        if (retPayment.getCode().equals(ReturnNo.OK)) {
            // ???????????????????????????????????????
            if (retPayment.getData().getState().equals(PaymentState.WAIT_PAY.getCode())) {
                WechatReturnObject<WechatPaymentQueryRetVo> wechatReturnObject
                        = wechatMicroService.queryPayment(bill.getOutTradeNo());
                WechatPaymentQueryRetVo wechatPaymentQueryRetVo = wechatReturnObject.getData();
                Payment payment = new Payment();
                // ??????paymentNotifyMessage?????????rocketMQ???????????????
                PaymentNotifyMessage message = new PaymentNotifyMessage();
                if (wechatPaymentQueryRetVo.getTradeState().equals(WechatTradeState.NOTPAY.getState())) {
                    // ???????????????????????????????????????
                    PaymentQueryMessage paymentQueryMessage = new PaymentQueryMessage();
                    paymentQueryMessage.setPaymentBill(bill);
                    messageProducer.sendPaymentQueryDelayedMessage(paymentQueryMessage);

                    // ??????
                    return;
                } else if (wechatPaymentQueryRetVo.getTradeState().equals(WechatTradeState.CLOSED.getState())) {
                    // ?????????
                    // ???????????????
                    payment.setId(paymentId);
                    payment.setState(PaymentState.FAIL.getCode());
                    transactionDao.updatePayment(payment);

                    message.setPaymentState(PaymentState.FAIL);
                } else if (wechatPaymentQueryRetVo.getTradeState().equals(WechatTradeState.SUCCESS.getState())) {
                    // ????????????
                    // ??????
                    // ???????????????
                    payment.setId(paymentId);
                    payment.setState(PaymentState.ALREADY_PAY.getCode());
                    payment.setPayTime(wechatPaymentQueryRetVo.getSuccessTime());
                    payment.setTradeSn(wechatPaymentQueryRetVo.getTransactionId());
                    payment.setActualAmount(wechatPaymentQueryRetVo.getAmount().getPayerTotal());
                    transactionDao.updatePayment(payment);

                    message.setPaymentState(PaymentState.ALREADY_PAY);
                }

                // ??????????????????????????????
                Map<String, Object> map = TransactionPatternFactory.decodeRequestNo(bill.getOutTradeNo());
                message.setDocumentId((String) map.get("documentId"));
                message.setDocumentType(Byte.parseByte((String) map.get("documentType")));
                messageProducer.sendPaymentNotifyMessage(message);
            }

        }
    }

    @Override
    public void queryRefund(RefundBill bill) {
        Long refundId = bill.getRelatedRefund().getId();
        ReturnObject<Refund> retRefund = transactionDao.getRefundById(refundId);

        if (retRefund.getCode().equals(ReturnNo.OK)) {
            if (retRefund.getData().getState().equals(RefundState.WAIT_REFUND.getCode())) {
                WechatReturnObject<WechatRefundQueryRetVo> wechatReturnObject =
                        wechatMicroService.queryRefund(bill.getOutRefundNo());
                WechatRefundQueryRetVo wechatRefundQueryRetVo = wechatReturnObject.getData();

                Refund refund = new Refund();
                // ??????refundMessage?????????rocketMQ???????????????
                RefundNotifyMessage message = new RefundNotifyMessage();
                if (wechatRefundQueryRetVo.getStatus().equals(WechatRefundState.SUCCESS.getState())) {
                    refund.setState(RefundState.FINISH_REFUND.getCode());
                    message.setRefundState(RefundState.FINISH_REFUND);
                } else {
                    refund.setState(RefundState.FAILED.getCode());
                    message.setRefundState(RefundState.FAILED);
                }

                // ???????????????
                refund.setId(refundId);
                refund.setRefundTime(wechatRefundQueryRetVo.getSuccessTime());
                refund.setTradeSn(wechatRefundQueryRetVo.getTransactionId());
                transactionDao.updateRefund(refund);

                // ??????????????????????????????
                Map<String, Object> map = TransactionPatternFactory.decodeRequestNo(bill.getOutRefundNo());
                message.setDocumentId((String) map.get("documentId"));
                message.setDocumentType(Byte.parseByte((String) map.get("documentType")));
                messageProducer.sendRefundNotifyMessage(message);
            }
        }
    }


    @Override
    public void closeTransaction(PaymentBill bill){
        wechatMicroService.closeTransaction(bill.getOutTradeNo());
    }

    @Override
    public String getFundFlowBill(String billDate) {
        WechatReturnObject<WeChatPayFundFlowBillRetVo> wechatReturnObject = wechatMicroService.getFundFlowBill(billDate);
        WeChatPayFundFlowBillRetVo weChatPayFundFlowBillRetVo = wechatReturnObject.getData();
        return weChatPayFundFlowBillRetVo.getDownloadUrl();
    }


    @Override
    public ReturnObject reconciliation(LocalDateTime beginTime,LocalDateTime endTime) {
        Integer success = 0;
        Integer error = 0;
        Integer extra = 0;
        //1.????????????????????????
        String url = getFundFlowBill("????????????");
        //TODO:???????????????????????????????????????zip
        FileUtil.unZip(new File("testfile/wechat/??????????????????(20211011-20211211).zip"), "testfile/wechat");
        List<WechatFormat> list = FileUtil.wechatParsing(new File("testfile/wechat/??????????????????(20211011-20211211)/??????????????????(20211011-20211211).csv"));
        //2.????????????????????????????????????
        for (WechatFormat wechatFormat : list) {
            //????????????
            if (!(wechatFormat.getTradeCreateTime().isAfter(beginTime) && wechatFormat.getTradeCreateTime().isBefore(endTime))) {
                break;
            }
            //?????????????????????Payment
            if (wechatFormat.getType().equals(WechatTypeState.REFUND)) {
                ReturnObject returnObject = transactionDao.getPaymentByTradeSn(wechatFormat.getTradeNo());
                if (!returnObject.getCode().equals(ReturnNo.OK)) {
                    return returnObject;
                }
                //?????????????????????,???????????????
                if (returnObject.getData() == null) {
                    ErrorAccount errorAccount = new ErrorAccount();
                    errorAccount.setTradeSn(wechatFormat.getTradeNo());
                    errorAccount.setPatternId(2L);
                    errorAccount.setIncome(wechatFormat.getAmount());
                    errorAccount.setExpenditure(0L);
                    errorAccount.setState((byte) 0);
                    errorAccount.setTime(wechatFormat.getTradeCreateTime());
                    errorAccount.setDocumentId(wechatFormat.getOutTradeNo());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                } else {
                    Payment payment = (Payment) returnObject.getData();
                    //??????????????????????????????
                    if (!(payment.getPayTime().isAfter(beginTime) && payment.getPayTime().isBefore(endTime))) {
                        break;
                    }
                    //????????????????????????
                    if (!payment.getActualAmount().equals(wechatFormat.getAmount())) {
                        ErrorAccount errorAccount = new ErrorAccount();
                        errorAccount.setTradeSn(wechatFormat.getTradeNo());
                        errorAccount.setPatternId(2L);
                        errorAccount.setIncome(wechatFormat.getAmount());
                        errorAccount.setExpenditure(0L);
                        errorAccount.setState((byte) 0);
                        errorAccount.setTime(wechatFormat.getTradeCreateTime());
                        errorAccount.setDocumentId(wechatFormat.getOutTradeNo());
                        ReturnObject returnObject1 = transactionDao.insertErrorAccount(errorAccount);
                        if (!returnObject1.getCode().equals(ReturnNo.OK.getCode())) {
                            return returnObject1;
                        }
                        error++;
                    }
                    //???????????????????????????
                    else {
                        payment.setState(PaymentState.ALREADY_RECONCILIATION.getCode());
                        ReturnObject returnObject1 = transactionDao.updatePayment(payment);
                        if (!returnObject1.getCode().equals(ReturnNo.OK.getCode())) {
                            return returnObject1;
                        }
                        success++;
                    }
                }
            }
            //?????????????????????refund
            else {
                ReturnObject returnObject = transactionDao.getRefundByTradeSn(wechatFormat.getTradeNo());
                if (!returnObject.getCode().equals(ReturnNo.OK)) {
                    return returnObject;
                }
                //???????????????????????????????????????
                if (returnObject.getData() == null) {
                    ErrorAccount errorAccount = new ErrorAccount();
                    errorAccount.setTradeSn(wechatFormat.getTradeNo());
                    errorAccount.setPatternId(2L);
                    errorAccount.setIncome(0L);
                    errorAccount.setExpenditure(wechatFormat.getAmount());
                    errorAccount.setState((byte) 0);
                    errorAccount.setTime(wechatFormat.getTradeCreateTime());
                    errorAccount.setDocumentId(wechatFormat.getOutTradeNo());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                } else {
                    Refund refund = (Refund) returnObject.getData();
                    //??????????????????????????????
                    if (!(refund.getRefundTime().isAfter(beginTime) && refund.getRefundTime().isBefore(endTime))) {
                        break;
                    }
                    //????????????????????????
                    if (!refund.getAmount().equals(wechatFormat.getAmount())) {
                        ErrorAccount errorAccount = new ErrorAccount();
                        errorAccount.setTradeSn(wechatFormat.getTradeNo());
                        errorAccount.setPatternId(2L);
                        errorAccount.setIncome(0L);
                        errorAccount.setExpenditure(wechatFormat.getAmount());
                        errorAccount.setState((byte) 0);
                        errorAccount.setTime(wechatFormat.getTradeCreateTime());
                        errorAccount.setDocumentId(wechatFormat.getOutTradeNo());
                        ReturnObject returnObject1 = transactionDao.insertErrorAccount(errorAccount);
                        if (!returnObject1.getCode().equals(ReturnNo.OK.getCode())) {
                            return returnObject1;
                        }
                        error++;
                    }
                    //???????????????????????????
                    else {
                        refund.setState(RefundState.FINISH_RECONCILIATION.getCode());
                        ReturnObject returnObject1 = transactionDao.updateRefund(refund);
                        if (!returnObject1.getCode().equals(ReturnNo.OK.getCode())) {
                            return returnObject1;
                        }
                        success++;
                    }

                }

            }

        }
        ReconciliationRetVo reconciliationRetVo = new ReconciliationRetVo();
        reconciliationRetVo.setError(error);
        reconciliationRetVo.setSuccess(success);
        reconciliationRetVo.setExtra(extra);
        return new ReturnObject(reconciliationRetVo);
    }
}