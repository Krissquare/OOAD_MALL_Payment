package cn.edu.xmu.oomall.transaction.util;


public abstract class TransactionPattern {

    public abstract void requestPayment(String requestNo, PaymentBill bill);

    public abstract void requestRefund(String requestNo, RefundBill bill);

}
