package cn.edu.xmu.oomall.ooad201.payment.controller;

import cn.edu.xmu.oomall.ooad201.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class PaymentController {
    @Autowired
    PaymentService paymentService;
}
