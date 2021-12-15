package cn.edu.xmu.oomall.transaction.util.alipay.microservice.bo;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/10 22:45
 */
public enum AlipayRefundState {
    REFUND_SUCCESS(0, "REFUND_SUCCESS");
    private int code;
    private String description;
    AlipayRefundState(int code, String description) {
        this.code=code;
        this.description=description;
    }
    public Integer getCode(){
        return code;
    }

    public String getDescription() {return description;}
}
