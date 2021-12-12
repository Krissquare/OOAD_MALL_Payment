package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/09 20:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlipayNotifyVo {
    /**
     * 回调时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private LocalDateTime notify_time;

    /**
     * 原支付请求的商户订单号。
     */
    private String out_trade_no;
    /**
     * 交易状态:
     * TRADE_SUCCESS（交易成功）
     * TRADE_FAILED（交易失败）
     */
    private String trade_status;

    /**
     *  退款通知中返回退款申请的流水号,只有退款回调时该字段不为空
     */
    private String out_biz_no;

    /**
     * 订单实际支付金额
     */
    private String buyer_pay_amount;


    /**
     * 通知的类型固定:trade_status_sync
     */
    private String notify_type="trade_status_sync";
    /**
     * 通知校验 ID 固定：ac05099524730693a8b330c5ecf72da9786
     */
    private String notify_id="ac05099524730693a8b330c5ecf72da9786";
    /**
     * 支付宝分配给开发者的应用 ID。固定:20214072300007148
     */
    private String app_id="20214072300007148";
    /**
     * 编码格式，如 utf-8、gbk、gb2312 等。固定:utf-8
     */
    private String charset="utf-8";
    /**
     * 版本：固定：1.0
     */
    private String version="1.0";
    /**
     * 固定：RSA2
     */
    private  String sign_type="RSA2";
    /**
     * 固定:601510b7970e52cc63db0f4497cf70e
     */
    private String sign="601510b7970e52cc63db0f4497cf70e";
    /**
     * 固定:2013112011001004330000121536
     */
    private String trade_no="2013112011001004330000121536";


    public AlipayNotifyVo(LocalDateTime notify_time, String out_trade_no, String trade_status, String out_biz_no) {
        this.notify_time = notify_time;
        this.out_trade_no = out_trade_no;
        this.trade_status = trade_status;
        this.out_biz_no = out_biz_no;
    }
}
