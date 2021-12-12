package cn.edu.xmu.oomall.transaction.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundAmountVo {
    private Integer refund;
    private Integer total;
    private String currency;
}
