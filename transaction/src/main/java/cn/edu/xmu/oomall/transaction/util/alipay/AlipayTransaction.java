package cn.edu.xmu.oomall.transaction.util.alipay;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.bo.*;
import cn.edu.xmu.oomall.transaction.model.vo.ReconciliationRetVo;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import cn.edu.xmu.oomall.transaction.util.TransactionPatternFactory;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.AlipayMicroService;
import cn.edu.xmu.oomall.transaction.util.alipay.microservice.vo.*;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayMethod;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayRefundState;
import cn.edu.xmu.oomall.transaction.util.alipay.model.bo.AlipayTradeState;
import cn.edu.xmu.oomall.transaction.util.billformatter.FileUtil;
import cn.edu.xmu.oomall.transaction.util.billformatter.vo.AliPayFormat;

import cn.edu.xmu.oomall.transaction.util.mq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AlipayTransaction extends TransactionPattern {

    @Autowired
    private AlipayMicroService alipayMicroService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private TransactionDao transactionDao;

    @Override
    public void requestPayment(PaymentBill bill) {
        AlipayPaymentVo paymentVo = new AlipayPaymentVo();
        paymentVo.setOutTradeNo(bill.getOutTradeNo());
        paymentVo.setTotalAmount(bill.getAmount());

        WarpRetObject warpRetObject = alipayMicroService.gatewayDo(null,
                AlipayMethod.PAY.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(paymentVo));
        AlipayPaymentRetVo alipayPaymentRetVo = warpRetObject.getAlipayPaymentRetVo();

        PaymentQueryMessage paymentQueryMessage = new PaymentQueryMessage();
        paymentQueryMessage.setPaymentBill(bill);
        messageProducer.sendPaymentQueryDelayedMessage(paymentQueryMessage);

    }

    @Override
    public void requestRefund(RefundBill bill) {
        AlipayRefundVo refundVo = new AlipayRefundVo();
        refundVo.setOutTradeNo(bill.getOutTradeNo());
        refundVo.setOutRequestNo(bill.getOutRefundNo());
        refundVo.setRefundAmount(bill.getAmount());

        WarpRetObject warpRetObject = alipayMicroService.gatewayDo(null,
                AlipayMethod.REFUND.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(refundVo));
        AlipayRefundRetVo alipayRefundRetVo = warpRetObject.getAlipayRefundRetVo();

        // ??????????????????????????????????????????
//        if (alipayRefundRetVo != null
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.REFUND_AMT_NOT_EQUAL_TOTAL.getSubCode())
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.TRADE_NOT_ALLOW_REFUND.getSubCode())
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.TRADE_NOT_EXIST.getSubCode())) {
// ?????????????????????????????????????????????????????????
//        }

        // ???????????????????????????????????????
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
                AlipayPaymentQueryVo queryVo = new AlipayPaymentQueryVo();
                queryVo.setOutTradeNo(bill.getOutTradeNo());

                WarpRetObject warpRetObject = alipayMicroService.gatewayDo(
                        null,
                        AlipayMethod.QUERY_PAY.getMethod(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JacksonUtil.toJson(queryVo));
                AlipayPaymentQueryRetVo alipayPaymentQueryRetVo = warpRetObject.getAlipayPaymentQueryRetVo();

                Payment payment = new Payment();
                // ??????paymentNotifyMessage?????????rocketMQ???????????????
                PaymentNotifyMessage message = new PaymentNotifyMessage();
                if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.WAIT_BUYER_PAY.getDescription())) {
                    // ???????????????????????????????????????
                    PaymentQueryMessage paymentQueryMessage = new PaymentQueryMessage();
                    paymentQueryMessage.setPaymentBill(bill);
                    messageProducer.sendPaymentQueryDelayedMessage(paymentQueryMessage);

                    // ??????
                    return;
                }
                else if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
                    // ?????????
                    // ???????????????
                    payment.setId(paymentId);
                    payment.setState(PaymentState.FAIL.getCode());
                    transactionDao.updatePayment(payment);

                    message.setPaymentState(PaymentState.FAIL);
                } else if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                        alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
                    // ????????????
                    // ??????
                    // ???????????????
                    payment.setId(paymentId);
                    payment.setState(PaymentState.ALREADY_PAY.getCode());
                    payment.setPayTime(alipayPaymentQueryRetVo.getSendPayDate());
                    payment.setTradeSn(alipayPaymentQueryRetVo.getTradeNo());
                    payment.setActualAmount(alipayPaymentQueryRetVo.getBuyerPayAmount());
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
                AlipayRefundQueryVo queryVo = new AlipayRefundQueryVo();
                queryVo.setOutRequestNo(bill.getOutRefundNo());
                queryVo.setOutTradeNo(bill.getOutTradeNo());

                WarpRetObject warpRetObject = alipayMicroService.gatewayDo(null,
                        AlipayMethod.QUERY_REFUND.getMethod(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JacksonUtil.toJson(queryVo));
                AlipayRefundQueryRetVo alipayRefundQueryRetVo = warpRetObject.getAlipayRefundQueryRetVo();

                Refund refund = new Refund();
                // ??????refundMessage?????????rocketMQ???????????????
                RefundNotifyMessage message = new RefundNotifyMessage();
                if (alipayRefundQueryRetVo.getRefundStatus() != null &&
                        alipayRefundQueryRetVo.getRefundStatus().equals(AlipayRefundState.REFUND_SUCCESS.getDescription())) {
                    refund.setState(RefundState.FINISH_REFUND.getCode());
                    message.setRefundState(RefundState.FINISH_REFUND);
                } else {
                    refund.setState(RefundState.FAILED.getCode());
                    message.setRefundState(RefundState.FAILED);
                }

                // ???????????????
                refund.setId(refundId);
                refund.setRefundTime(alipayRefundQueryRetVo.getGmtRefundPay());
                refund.setTradeSn(alipayRefundQueryRetVo.getTradeNo());
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
        AlipayCloseVo alipayCloseVo = new AlipayCloseVo();
        alipayCloseVo.setOutTradeNo(bill.getOutTradeNo());
        alipayMicroService.gatewayDo(null,
                AlipayMethod.CLOSE.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(alipayCloseVo));
    }

    @Override
    public String getFundFlowBill(String billDate) {
        WarpRetObject warpRetObject = alipayMicroService.gatewayDo(null,
                AlipayMethod.QUERY_DOWNLOAD_BILL.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        DownloadUrlQueryRetVo downloadUrlQueryRetVo = warpRetObject.getDownloadUrlQueryRetVo();
        return downloadUrlQueryRetVo.getBillDownloadUrl();
    }


    @Override
    public ReturnObject reconciliation(LocalDateTime beginTime,LocalDateTime endTime){
        Integer success=0;
        Integer error=0;
        Integer extra=0;
        //1.?????????????????????
        String url=getFundFlowBill("????????????");
        //TODO:????????????????????????????????????zip

        FileUtil.unZip(new File("testfile/alipay/202111_2088202991815014.zip"), "testfile/alipay");
        List<AliPayFormat> list = FileUtil.aliPayParsing(new File("testfile/alipay/20882029918150140156_202111_????????????_1.csv"));
        //2.????????????????????????????????????
        for(AliPayFormat aliPayFormat:list){
            //????????????
            if(!(aliPayFormat.getTradeCreateTime().isAfter(beginTime)&&aliPayFormat.getTradeCreateTime().isBefore(endTime))){
                break;
            }
            //?????????????????????Payment
            if(aliPayFormat.getIncome()>0){
                ReturnObject returnObject=transactionDao.getPaymentByTradeSn(aliPayFormat.getAccountSerialNumber());
                if(!returnObject.getCode().equals(ReturnNo.OK)){
                    return returnObject;
                }
                //?????????????????????,???????????????
                if(returnObject.getData()==null){
                    ErrorAccount errorAccount=new ErrorAccount();
                    errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                    errorAccount.setPatternId(1L);
                    errorAccount.setIncome(aliPayFormat.getIncome());
                    errorAccount.setExpenditure(aliPayFormat.getOutlay());
                    errorAccount.setState((byte)0);
                    errorAccount.setTime(aliPayFormat.getTradeCreateTime());
                    errorAccount.setDocumentId(aliPayFormat.getOutTradeNo());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                }
                else{
                    Payment payment=(Payment)returnObject.getData();
                    //??????????????????????????????
                    if(!(payment.getPayTime().isAfter(beginTime)&&payment.getPayTime().isBefore(endTime))){
                        break;
                    }
                    //????????????????????????
                    if(!payment.getActualAmount().equals(aliPayFormat.getIncome())){
                        ErrorAccount errorAccount=new ErrorAccount();
                        errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                        errorAccount.setPatternId(1L);
                        errorAccount.setIncome(aliPayFormat.getIncome());
                        errorAccount.setExpenditure(aliPayFormat.getOutlay());
                        errorAccount.setState((byte)0);
                        errorAccount.setTime(aliPayFormat.getTradeCreateTime());
                        errorAccount.setDocumentId(aliPayFormat.getOutTradeNo());
                        ReturnObject returnObject1=transactionDao.insertErrorAccount(errorAccount);
                        if(!returnObject1.getCode().equals(ReturnNo.OK.getCode()))
                        {
                            return returnObject1;
                        }
                        error++;
                    }
                    //???????????????????????????
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
            //?????????????????????refund
            else{
                ReturnObject returnObject=transactionDao.getRefundByTradeSn(aliPayFormat.getAccountSerialNumber());
                if(!returnObject.getCode().equals(ReturnNo.OK)){
                    return returnObject;
                }
                //???????????????????????????????????????
                if(returnObject.getData()==null){
                    ErrorAccount errorAccount=new ErrorAccount();
                    errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                    errorAccount.setPatternId(1L);
                    errorAccount.setIncome(aliPayFormat.getIncome());
                    errorAccount.setExpenditure(aliPayFormat.getOutlay());
                    errorAccount.setState((byte)0);
                    errorAccount.setTime(aliPayFormat.getTradeCreateTime());
                    errorAccount.setDocumentId(aliPayFormat.getOutTradeNo());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                }
                else{
                    Refund refund=(Refund)returnObject.getData();
                    //??????????????????????????????
                    if(!(refund.getRefundTime().isAfter(beginTime)&&refund.getRefundTime().isBefore(endTime))){
                        break;
                    }
                    //????????????????????????
                    if(!refund.getAmount().equals(aliPayFormat.getOutlay())){
                        ErrorAccount errorAccount=new ErrorAccount();
                        errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                        errorAccount.setPatternId(1L);
                        errorAccount.setIncome(aliPayFormat.getIncome());
                        errorAccount.setExpenditure(aliPayFormat.getOutlay());
                        errorAccount.setState((byte)0);
                        errorAccount.setTime(aliPayFormat.getTradeCreateTime());
                        errorAccount.setDocumentId(aliPayFormat.getOutTradeNo());
                        ReturnObject returnObject1=transactionDao.insertErrorAccount(errorAccount);
                        if(!returnObject1.getCode().equals(ReturnNo.OK.getCode()))
                        {
                            return returnObject1;
                        }
                        error++;
                    }
                    //???????????????????????????
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
