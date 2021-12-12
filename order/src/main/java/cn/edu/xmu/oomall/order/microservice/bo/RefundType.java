package cn.edu.xmu.oomall.order.microservice.bo;

public enum RefundType {
    ORDER((byte)0,"订单"),
    DEPOSIT((byte)1,"保证金"),
    AFTERSALE((byte)2,"售后");
    private Byte code;
    private String message;
    RefundType(Byte code, String message){
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
