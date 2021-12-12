package cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/10 21:43
 */
public enum WechatTradeState {
    SUCCESS("SUCCESS"),
    CLOSED("CLOSED"),
    REFUND("REFUND"),
    NOTPAY("NOTPAY");
    private String state;
    WechatTradeState(String state){
        this.state = state;
    }
    public String getState(){
        return state;
    }
}
