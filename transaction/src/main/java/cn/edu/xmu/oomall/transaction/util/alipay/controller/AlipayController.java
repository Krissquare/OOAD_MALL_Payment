package cn.edu.xmu.oomall.transaction.util.alipay.controller;

import cn.edu.xmu.oomall.transaction.util.alipay.model.vo.AlipayNotifyVo;
import cn.edu.xmu.oomall.transaction.util.alipay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class AlipayController {

    @Autowired
    private AlipayService alipayService;
    /**
     * gyt
     * 阿里异步t通知API
     * @param alipayNotifyVo
     * @return
     */
    @PostMapping("/alipay/notify")
    public Object notifyByAlipay(@RequestBody AlipayNotifyVo alipayNotifyVo) {

        if (alipayNotifyVo.getOutBizNo() == null) {
            alipayService.paymentNotifyByAlipay(alipayNotifyVo);
        } else {
            alipayService.refundNotifyByAlipay(alipayNotifyVo);
        }

        return null;
    }
}
