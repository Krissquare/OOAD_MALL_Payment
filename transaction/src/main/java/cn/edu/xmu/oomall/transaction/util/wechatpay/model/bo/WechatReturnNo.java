package cn.edu.xmu.oomall.transaction.util.wechatpay.model.bo;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
public enum WechatReturnNo {

    //200
    OK("成功"),

    //400
    ORDER_CLOSED("订单已关闭"),
    ORDER_PAID("订单已支付"),
    PARAM_ERROR("参数错误"),

    //403
    OUT_TRADE_NO_USED("商户订单号重复"),
    OUT_REFUND_NO_USED("商户退款单号重复"),
    REFUND_TRANSACTION_ERROR("对应支付单未成功支付"),
    REFUND_AMOUNT_ERROR("退款金额错误"),

    //404
    RESOURCE_NOT_EXISTS("查询的资源不存在"),

    //500
    SYSTEM_ERROR("系统错误");

    private String message;

    WechatReturnNo(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
