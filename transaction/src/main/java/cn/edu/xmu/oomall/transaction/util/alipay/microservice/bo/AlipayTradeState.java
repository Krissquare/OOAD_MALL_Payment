package cn.edu.xmu.oomall.transaction.util.alipay.microservice.bo;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/10 22:45
 */
public enum AlipayTradeState {
    TRADE_CLOSED(0, "TRADE_CLOSED"),
    TRADE_SUCCESS(1, "TRADE_SUCCESS"),
    WAIT_BUYER_PAY(2, "WAIT_BUYER_PAY"),
    TRADE_FINISHED(3, "TRADE_FINISHED");
    private int code;
    private String description;

    AlipayTradeState(int code, String description) {
        this.code=code;
        this.description=description;
    }
    public Integer getCode(){
        return code;
    }
    public String getDescription() {return description;}
}
