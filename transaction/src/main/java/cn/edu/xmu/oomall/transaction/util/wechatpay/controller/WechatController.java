package cn.edu.xmu.oomall.transaction.util.wechatpay.controller;

import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatPaymentNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo.WechatRefundNotifyVo;
import cn.edu.xmu.oomall.transaction.util.wechatpay.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class WechatController {

    @Autowired
    private WechatService wechatService;
    /**
     * gyt
     * 微信支付通知API
     * @param wechatPaymentNotifyVo
     * @return
     */
    @PostMapping("/wechat/payment/notify")
    public Object paymentNotifyByWechat(@RequestBody WechatPaymentNotifyVo wechatPaymentNotifyVo){
        return wechatService.paymentNotifyByWechat(wechatPaymentNotifyVo);
    }

    /**
     * gyt
     * 微信退款通知API
     * @param wechatRefundNotifyVo
     * @return
     */
    @PostMapping("/wechat/refund/notify")
    public Object refundNotifyByWechat(@RequestBody WechatRefundNotifyVo wechatRefundNotifyVo){
        return wechatService.refundNotifyByWechat(wechatRefundNotifyVo);
    }

}
