package cn.edu.xmu.oomall.transaction.util.alipay.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/09 21:14
 */
@Data
public class WechatNotifyRetVo {
    String code;
    String message;
    public WechatNotifyRetVo(){
        this.code="success";
        this.message="成功";
    }
}
