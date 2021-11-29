package cn.edu.xmu.oomall.ooad201.payment.service;

import cn.edu.xmu.oomall.ooad201.payment.dao.PaymentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentDao paymentDao;
}
