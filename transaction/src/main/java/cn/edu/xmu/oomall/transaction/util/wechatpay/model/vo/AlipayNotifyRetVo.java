package cn.edu.xmu.oomall.transaction.util.wechatpay.model.vo;

import lombok.Data;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/09 21:14
 */
@Data
public class AlipayNotifyRetVo {
    String code;
    String message;
    public AlipayNotifyRetVo(){
        this.code="success";
        this.message="成功";
    }
}
