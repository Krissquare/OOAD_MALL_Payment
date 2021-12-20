package cn.edu.xmu.oomall.transaction.util.billformatter.bo;

import lombok.Getter;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/17 14:26
 */
@Getter
public enum WechatTypeState {
    /**
     * 微信的当前状态字段
     * 我看支出是对于我自己来说我付款成功
     *支出 收入
     */
    PAY(0, "支出"),
    REFUND(1, "收入");
    private int code;
    private String description;
    WechatTypeState(int code, String description) {
        this.code=code;
        this.description=description;
    }
}
