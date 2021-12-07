package cn.edu.xmu.oomall.ooad201.payment.dao;

import cn.edu.xmu.oomall.ooad201.payment.mapper.PaymentPatternPoMapper;
import cn.edu.xmu.oomall.ooad201.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.oomall.ooad201.payment.mapper.RefundPoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao {
    @Autowired
    ErrorPaymentPoMapper errorPaymentPoMapper;
    @Autowired
    PaymentPatternPoMapper paymentPatternPoMapper;
    @Autowired
    PaymentPoMapper paymentPoMapper;
    @Autowired
    RefundPoMapper refundPoMapper;


}
