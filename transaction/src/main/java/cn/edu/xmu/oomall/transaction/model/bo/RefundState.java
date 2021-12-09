package cn.edu.xmu.oomall.ooad201.payment.model.bo;

public enum RefundState {
    WAIT_REFUND(0,"待退款"),
    FINISH_REFUND(1,"已退款"),
    FINISH_RECONCILIATION(2,"已对账"),
    FINISH_LIQUIDATION(3,"已清算"),
    CANCEL_REFUND(4,"已取消"),
    FAILED(5,"失败");
    private int code;
    private String message;
    RefundState(int code, String message){
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }
}
