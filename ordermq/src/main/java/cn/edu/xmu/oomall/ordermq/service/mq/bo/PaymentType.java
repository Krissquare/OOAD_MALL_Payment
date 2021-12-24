package cn.edu.xmu.oomall.ordermq.service.mq.bo;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/09 14:15
 */
public enum PaymentType {
    ORDER((byte)0,"订金"),
    DEPOSIT((byte)1,"保证金"),
    ORDER_ADVANCE((byte)2,"订单订金"),
    ORDER_REST((byte)3,"订单尾款");
    private Byte code;
    private String type;
    PaymentType(Byte code, String type){
        this.code = code;
        this.type = type;
    }
    public Byte getCode() {
        return code;
    }

    public String getType(){
        return type;
    }
}
