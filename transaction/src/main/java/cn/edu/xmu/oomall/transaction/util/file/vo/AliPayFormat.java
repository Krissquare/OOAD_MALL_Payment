package cn.edu.xmu.oomall.transaction.util.file.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/17 8:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AliPayFormat {
    /**
     * 账务流水号
     */
    String accountSerialNumber;
    /**
     * 业务流水号
     */
    String tradeNo;
    /**
     * 商户订单号
     */
    String outTradeNo;
    /**
     * 商品名称
     */
    String goodsName;
    /**
     * 发生时间
     */
    LocalDateTime tradeCreateTime;
    /**
     * 对方账号
     */
    String transNo;
    /**
     * 收入
     */
    String income;
    /**
     * 支出
     */
    String outlay;
    /**
     * 账户余额
     */
    String balance;
    /**
     * 交易渠道
     */
    String tradingChannel;
    /**
     * 业务类型
     */
    String businessType;
    /**
     * 备注
     */
    String remark;
}
