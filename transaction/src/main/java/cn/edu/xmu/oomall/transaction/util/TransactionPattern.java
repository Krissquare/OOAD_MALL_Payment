package cn.edu.xmu.oomall.transaction.util;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.model.vo.RequestPaymentVo;


public abstract class TransactionPattern {

    public abstract ReturnObject requestPayment(Long requestNo, PaymentBill bill);

    public abstract ReturnObject requestRefund(Long requestNo, RefundBill bill);

}
