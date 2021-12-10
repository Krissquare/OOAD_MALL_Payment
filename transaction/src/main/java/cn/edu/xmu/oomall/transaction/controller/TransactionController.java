package cn.edu.xmu.oomall.transaction.controller;

import cn.edu.xmu.oomall.transaction.model.vo.AlipayNotifyVo;
import cn.edu.xmu.oomall.transaction.model.vo.WechatPaymentNotifyVo;
import cn.edu.xmu.oomall.transaction.model.vo.WechatRefundNotifyVo;
import cn.edu.xmu.oomall.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    /**
     * /wechat/payment/notify微信支付通知API
     * @param signature
     * @param wechatPaymentNotifyVo
     * @return
     */
    @PostMapping("/wechat/payment/notify")
    private Object paymentNotifyByWechat(@RequestHeader("Wechatpay-Signature")String signature,
                                         @RequestBody WechatPaymentNotifyVo wechatPaymentNotifyVo){
        transactionService.paymentNotifyByWechat(wechatPaymentNotifyVo);
        return wechatPaymentNotifyVo;
    }

    /**
     * /wechat/refund/notify微信退款通知API
     * @param signature
     * @param wechatRefundNotifyVo
     * @return
     */
    @PostMapping("/wechat/refund/notify")
    public Object refundNotifyByWechat(@RequestHeader("Wechatpay-Signature")String signature,
                                       @RequestBody WechatRefundNotifyVo wechatRefundNotifyVo){
        return null;
    }

    @PostMapping("/alipay/notify")
    public Object notifyByAlipay(@RequestBody AlipayNotifyVo alipayNotifyVo){
        return null;
    }
}
