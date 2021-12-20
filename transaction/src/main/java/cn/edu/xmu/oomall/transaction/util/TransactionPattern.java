package cn.edu.xmu.oomall.transaction.util;


import cn.edu.xmu.oomall.core.util.ReturnObject;
import java.time.LocalDateTime;

public abstract class TransactionPattern {

    public abstract void requestPayment(PaymentBill bill);

    public abstract void requestRefund(RefundBill bill);

    public abstract void queryPayment(PaymentBill bill);

    public abstract void queryRefund(RefundBill bill);

    public abstract void closeTransaction(PaymentBill bill);

    public abstract String getFundFlowBill(String billDate);

    public abstract ReturnObject reconciliation(LocalDateTime beginTime, LocalDateTime endTime);

}
