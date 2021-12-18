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
import cn.edu.xmu.oomall.transaction.util.file.FileUtil;
import cn.edu.xmu.oomall.transaction.util.file.vo.AliPayFormat;
import cn.edu.xmu.oomall.transaction.util.mq.ActiveQueryMessage;
import cn.edu.xmu.oomall.transaction.util.mq.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AlipayTransaction extends TransactionPattern {

    @Autowired
    private AlipayMicroService alipayMicroService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private TransactionPatternFactory transactionPatternFactory;


    @Override
    public void requestPayment(String requestNo, PaymentBill bill) {
        AlipayPaymentVo paymentVo = new AlipayPaymentVo();
        paymentVo.setOutTradeNo(requestNo);
        paymentVo.setTotalAmount(bill.getAmount());

        Object object = alipayMicroService.gatewayDo(null,
                AlipayMethod.PAY.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(paymentVo));

       // AlipayPaymentRetVo alipayPaymentRetVo = warpRetObject.getAlipayPaymentRetVo();
        ActiveQueryMessage queryMessage = new ActiveQueryMessage();
        queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERY_PAYMENT);
        queryMessage.setRequestNo(requestNo);
        queryMessage.setPatternId(bill.getPatternId());
        queryMessage.setBill(bill);
        messageProducer.sendActiveQueryDelayedMessage(queryMessage);
    }

    @Override
    public void requestRefund(String requestNo, RefundBill bill) {
        AlipayRefundVo refundVo = new AlipayRefundVo();
        refundVo.setOutTradeNo(bill.getPaymentId().toString());
        refundVo.setOutRequestNo(requestNo);
        refundVo.setRefundAmount(bill.getAmount());

        AlipayRefundRetVo alipayRefundRetVo = (AlipayRefundRetVo) alipayMicroService.gatewayDo(null,
                AlipayMethod.QUERY_REFUND.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(refundVo));

        // 同步处理，在支持退款的情况下
//        if (alipayRefundRetVo != null
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.REFUND_AMT_NOT_EQUAL_TOTAL.getSubCode())
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.TRADE_NOT_ALLOW_REFUND.getSubCode())
//        && !alipayRefundRetVo.getSubCode().equals(AlipayReturnNo.TRADE_NOT_EXIST.getSubCode())) {
// 同步回传没有成功时间，还是靠主动查询吧
//        }


        // 发送主动查询退款的延时消息
        ActiveQueryMessage queryMessage = new ActiveQueryMessage();
        queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERT_REFUND);
        queryMessage.setRequestNo(requestNo);
        queryMessage.setOutTradeNo(bill.getPaymentId().toString());
        queryMessage.setPatternId(bill.getPatternId());
        queryMessage.setBill(bill);
        messageProducer.sendActiveQueryDelayedMessage(queryMessage);
    }


    @Override
    public void queryPayment(String requestNo, PaymentBill bill) {
        String paymentId = (String) transactionPatternFactory
                .decodeRequestNo(requestNo).get("id");

        ReturnObject<Payment> retPayment = transactionDao.getPaymentById(Long.parseLong(paymentId));
        if (retPayment.getCode().equals(ReturnNo.OK)) {
            // 待支付的情况下，才主动查询
            if (retPayment.getData().getState().equals(PaymentState.WAIT_PAY.getCode())) {
                AlipayPaymentQueryVo queryVo = new AlipayPaymentQueryVo();
                queryVo.setOutTradeNo(requestNo);

                AlipayPaymentQueryRetVo alipayPaymentQueryRetVo = (AlipayPaymentQueryRetVo) alipayMicroService.gatewayDo(null,
                        AlipayMethod.QUERY_PAY.getMethod(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JacksonUtil.toJson(queryVo));

                Payment payment = new Payment();
                if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.WAIT_BUYER_PAY.getDescription())) {
                    // 发送主动查询支付的延时消息
                    ActiveQueryMessage queryMessage = new ActiveQueryMessage();
                    queryMessage.setMessageType(ActiveQueryMessage.QueryMessageType.QUERY_PAYMENT);
                    queryMessage.setRequestNo(requestNo);
                    queryMessage.setPatternId(bill.getPatternId());
                    messageProducer.sendActiveQueryDelayedMessage(queryMessage);
                }
                else if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_CLOSED.getDescription())) {
                    // 已关闭
                    payment.setState(PaymentState.CANCEL.getCode());
                } else if (alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_FINISHED.getDescription()) ||
                        alipayPaymentQueryRetVo.getTradeStatus().equals(AlipayTradeState.TRADE_SUCCESS.getDescription())) {
                    // 交易结束
                    // 成功
                    payment.setState(PaymentState.ALREADY_PAY.getCode());
                }

                payment.setId(Long.parseLong(paymentId));
                payment.setPayTime(alipayPaymentQueryRetVo.getSendPayDate());
                payment.setTradeSn(alipayPaymentQueryRetVo.getTradeNo());
                payment.setActualAmount(alipayPaymentQueryRetVo.getBuyerPayAmount());
                transactionDao.updatePayment(payment);
            }
        }
    }

    @Override
    public void queryRefund(String requestNo , RefundBill bill) {

        String refundId = (String) transactionPatternFactory
                .decodeRequestNo(requestNo).get("id");
        ReturnObject<Refund> retRefund = transactionDao.getRefundById(Long.parseLong(refundId));

        if (retRefund.getCode().equals(ReturnNo.OK)) {
            if (retRefund.getData().getState().equals(RefundState.WAIT_REFUND.getCode())) {
                AlipayRefundQueryVo queryVo = new AlipayRefundQueryVo();
                queryVo.setOutRequestNo(requestNo);
                queryVo.setOutTradeNo(bill.getPaymentId().toString());

                AlipayRefundQueryRetVo alipayRefundQueryRetVo = (AlipayRefundQueryRetVo) alipayMicroService.gatewayDo(null,
                        AlipayMethod.QUERY_REFUND.getMethod(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        JacksonUtil.toJson(queryVo));

                Refund refund = new Refund();
                if (alipayRefundQueryRetVo.getRefundStatus().equals(AlipayRefundState.REFUND_SUCCESS.getDescription())) {
                    refund.setState(RefundState.FINISH_REFUND.getCode());
                } else {
                    refund.setState(RefundState.FAILED.getCode());
                }

                refund.setId(Long.parseLong(refundId));
                refund.setRefundTime(alipayRefundQueryRetVo.getGmtRefundPay());
                refund.setTradeSn(alipayRefundQueryRetVo.getTradeNo());
                transactionDao.updateRefund(refund);
            }
        }
    }
    @Override
    public void closeTransaction(String requestNo){
        AlipayPaymentQueryVo queryVo = new AlipayPaymentQueryVo();
        queryVo.setOutTradeNo(requestNo);
        alipayMicroService.gatewayDo(null,
                AlipayMethod.CLOSE.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                JacksonUtil.toJson(queryVo));
    }

    @Override
    public String getFundFlowBill(String billDate){
        DownloadUrlQueryRetVo downloadUrlQueryRetVo = (DownloadUrlQueryRetVo) alipayMicroService.gatewayDo(null,
                AlipayMethod.QUERY_DOWNLOAD_BILL.getMethod(),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        return downloadUrlQueryRetVo.getBillDownloadUrl();
    }
    @Override
    public ReturnObject reconciliation(LocalDateTime beginTime,LocalDateTime endTime){
        Integer success=0;
        Integer error=0;
        Integer extra=0;
        //1.提取支付宝流水
        String url=getFundFlowBill("这个没用");
        //TODO:拿到下载地址后下载，得到zip

        FileUtil.unZip(new File("testfile/alipay/202111_2088202991815014.zip"), "testfile/alipay");
        List<AliPayFormat> list = FileUtil.aliPayParsing(new File("testfile/alipay/20882029918150140156_202111_账务明细_1.csv"));
        //2.遍历支付宝流水，进行对账
        for(AliPayFormat aliPayFormat:list){
            //时间不符
            if(!(aliPayFormat.getTradeCreateTime().isAfter(beginTime)&&aliPayFormat.getTradeCreateTime().isBefore(endTime))){
                break;
            }
            //平台收入，对应Payment
            if(aliPayFormat.getIncome()>0){
                ReturnObject returnObject=transactionDao.getPaymentByTradeSn(aliPayFormat.getAccountSerialNumber());
                if(!returnObject.getCode().equals(ReturnNo.OK)){
                    return returnObject;
                }
                //商城没有：长账,插入错误账
                if(returnObject.getData()==null){
                    ErrorAccount errorAccount=new ErrorAccount();
                    errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                    errorAccount.setPatternId(1L);
                    errorAccount.setIncome(aliPayFormat.getIncome());
                    errorAccount.setExpenditure(aliPayFormat.getOutlay());
                    errorAccount.setState((byte)0);
                    errorAccount.setTime(aliPayFormat.getTradeCreateTime());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                }
                else{
                    Payment payment=(Payment)returnObject.getData();
                    //错账，插入错误账
                    if(!payment.getActualAmount().equals(aliPayFormat.getIncome())){
                        ErrorAccount errorAccount=new ErrorAccount();
                        errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                        errorAccount.setPatternId(1L);
                        errorAccount.setIncome(aliPayFormat.getIncome());
                        errorAccount.setExpenditure(aliPayFormat.getOutlay());
                        errorAccount.setState((byte)0);
                        errorAccount.setTime(aliPayFormat.getTradeCreateTime());
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
                ReturnObject returnObject=transactionDao.getRefundByTradeSn(aliPayFormat.getAccountSerialNumber());
                if(!returnObject.getCode().equals(ReturnNo.OK)){
                    return returnObject;
                }
                //商城没有：长账，插入错误账
                if(returnObject.getData()==null){
                    ErrorAccount errorAccount=new ErrorAccount();
                    errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                    errorAccount.setPatternId(1L);
                    errorAccount.setIncome(aliPayFormat.getIncome());
                    errorAccount.setExpenditure(aliPayFormat.getOutlay());
                    errorAccount.setState((byte)0);
                    errorAccount.setTime(aliPayFormat.getTradeCreateTime());
                    transactionDao.insertErrorAccount(errorAccount);
                    extra++;
                }
                else{
                    Refund refund=(Refund)returnObject.getData();
                    //错账，插入错误账
                    if(!refund.getAmount().equals(aliPayFormat.getOutlay())){
                        ErrorAccount errorAccount=new ErrorAccount();
                        errorAccount.setTradeSn(aliPayFormat.getTradeNo());
                        errorAccount.setPatternId(1L);
                        errorAccount.setIncome(aliPayFormat.getIncome());
                        errorAccount.setExpenditure(aliPayFormat.getOutlay());
                        errorAccount.setState((byte)0);
                        errorAccount.setTime(aliPayFormat.getTradeCreateTime());
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
