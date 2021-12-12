package cn.edu.xmu.oomall.transaction.util;

/**
 * 支付宝业务错误返回码
 */
public enum AlipayReturnNo {
    /**
     * 通用返回码
     */
    SYSTEM_ERROR("ACQ.SYSTEM_ERROR","系统错误"),

    /**
     * 支付
     */
    TRADE_HAS_SUCCESS("ACQ.TRADE_HAS_SUCCESS","交易已被支付"),
    TRADE_HAS_CLOSE("ACQ.TRADE_HAS_CLOSE","交易已关闭"),
    WAIT_BUYER_PAY("ACQ.WAIT_BUYER_PAY","交易创建"),
    TRADE_FINISHED("ACQ.TRADE_FINISHED","交易完结"),
    /**
     * 支付查询
     */
    TRADE_NOT_EXIST("ACQ.TRADE_NOT_EXIST","交易不存在"),
    /**
     * 关单,确认交易状态，非待支付状态(WAIT_BUYER_PAY)下不支持关单操作
     */
    REASON_TRADE_STATUS_INVALID("ACQ.REASON_TRADE_STATUS_INVALID","交易状态不合法"),
    /**
     * 退款:TRADE_HAS_CLOSE("ACQ.TRADE_HAS_CLOSE","交易已关闭") 上方
     */
    REFUND_AMT_NOT_EQUAL_TOTAL("ACQ.REFUND_AMT_NOT_EQUAL_TOTAL","退款金额超限"),
    TRADE_NOT_ALLOW_REFUND("ACQ.TRADE_NOT_ALLOW_REFUND","当前交易不允许退款");
    /**
     * 退款查询只可能返回:TRADE_NOT_EXIST("ACQ.TRADE_NOT_EXIST","交易不存在"),
     */
    private String subCode;
    private String subMsg;
    AlipayReturnNo(String subCode, String message){
        this.subCode = subCode;
        this.subMsg = message;
    }

    public String getSubCode() {
        return subCode;
    }

    public String getSubMsg() {
        return subMsg;
    }
}