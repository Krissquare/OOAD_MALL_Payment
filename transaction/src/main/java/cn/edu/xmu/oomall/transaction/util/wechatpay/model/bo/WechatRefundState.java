package cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/10 22:06
 */
public enum WechatRefundState {
    SUCCESS("SUCCESS"),
    ABNORMAL("ABNORMAL");
    private String state;
    WechatRefundState(String state){
        this.state = state;
    }
    public String getState(){
        return state;
    }
}
