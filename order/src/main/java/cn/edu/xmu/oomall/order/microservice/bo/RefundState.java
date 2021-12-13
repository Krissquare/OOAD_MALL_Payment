package cn.edu.xmu.oomall.order.microservice.bo;

public enum RefundState {
    WAIT_REFUND((byte)0,"待退款"),
    FINISH_REFUND((byte)1,"已退款"),
    FINISH_RECONCILIATION((byte)2,"已对账"),
    FINISH_LIQUIDATION((byte)3,"已清算"),
    CANCEL_REFUND((byte)4,"已取消"),
    FAILED((byte)5,"失败");
    private Byte code;
    private String message;
    RefundState(Byte code, String message){
        this.code = code;
        this.message = message;
    }
    public Byte getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }
}
