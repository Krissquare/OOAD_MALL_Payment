package cn.edu.xmu.oomall.transaction.model.bo;

import lombok.Getter;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/24 14:01
 */
public enum PaymentPatternState {
    VALAID((byte)0,"有效"),
    NOT_VALAID((byte)1,"无效");
    private Byte code;
    private String state;
    PaymentPatternState(Byte code, String type){
        this.code = code;
        this.state = type;
    }
    public Byte getCode() {
        return code;
    }

    public String getState(){
        return state;
    }
}
