package cn.edu.xmu.oomall.transaction.util.file.vo;

import cn.edu.xmu.oomall.transaction.util.file.bo.WechatTypeState;
import cn.edu.xmu.oomall.transaction.util.file.bo.WechatState;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WechatFormat {
    /**
     * 交易时间
     */
    LocalDateTime tradeCreateTime;
    /**
     * 交易类型
     */
    String businessType;
    /**
     * 对方账号
     */
    String transNo;
    /**
     * 商品
     */
    String goods;
    /**
     * 收支
     */
    WechatTypeState type;
    /**
     * 金额 单位分
     */
    Long amount;
    /**
     * 支付方式
     */
    String tradingChannel;
    /**
     * 当前状态
     */
    WechatState state;
    /**
     * 业务流水号
     */
    String tradeNo;
    /**
     * 商户订单号
     */
    String outTradeNo;
    /**
     * 备注
     */
    String remark;
}
