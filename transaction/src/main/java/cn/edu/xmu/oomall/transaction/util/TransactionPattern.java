package cn.edu.xmu.oomall.transaction.util;


import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.model.vo.ReconciliationRetVo;

import java.time.LocalDateTime;

public abstract class TransactionPattern {

    public abstract void requestPayment(String requestNo, PaymentBill bill);

    public abstract void requestRefund(String requestNo, RefundBill bill);

    public abstract void queryPayment(String requestNo, PaymentBill bill);

    public abstract void queryRefund(String requestNo, RefundBill bill);

    public abstract void closeTransaction(String requestNo);

    public abstract String getFundFlowBill(String billDate);

    public abstract ReturnObject reconciliation(LocalDateTime beginTime, LocalDateTime endTime);

}
