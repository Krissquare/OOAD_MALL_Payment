package cn.edu.xmu.oomall.transaction.model.bo;

import lombok.Getter;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/09 20:32
 */
@Getter
public enum PaymentState {
    WAIT_PAY((byte)0,"待支付"),
    ALREADY_PAY((byte)1,"已支付"),
    ALREADY_RECONCILIATION((byte)2,"已对账"),
    ALREADY_LIQUIDATION((byte)3,"已清算"),
    FAIL((byte)5,"失败");
    private Byte code;
    private String state;
    PaymentState(Byte code, String state){
        this.code = code;
        this.state = state;
    }
    public Byte getCode() {
        return code;
    }

    public String getState(){
        return state;
    }
}
