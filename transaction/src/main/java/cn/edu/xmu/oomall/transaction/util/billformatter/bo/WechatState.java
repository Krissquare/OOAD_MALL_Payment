package cn.edu.xmu.oomall.transaction.util.billformatter.bo;

import lombok.Getter;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/17 14:34
 */
@Getter
public enum WechatState {
    /**
     * 已全额退款
     * 支付成功
     * 已转账
     */
    FULLY_REFUND(0,"已全额退款"),
    PAY_SUCCESS(1,"支付成功"),
    TRANSFER_ACCOUNT(2,"已转账");
    private int code;
    private String description;
    WechatState(int code, String description) {
        this.code=code;
        this.description=description;
    }

}
