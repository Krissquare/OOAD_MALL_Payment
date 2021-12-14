package cn.edu.xmu.oomall.transaction.util.alipay.microservice.bo;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/12 0:40
 */
public enum AlipayMethod {
    PAY("alipay.trade.wap.pay"),
    QUERY_PAY("alipay.trade.query"),
    CLOSE("alipay.trade.close"),
    REFUND("alipay.trade.refund"),
    QUERY_REFUND("alipay.trade.refund.query"),
    QUERY_DOWNLOAD_BILL("alipay.data.dataservice.bill.downloadurl.query");
    private String method;
    AlipayMethod(String method){
        this.method = method;
    }
    public String getMethod(){
        return method;
    }
}
