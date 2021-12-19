package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.util.RefundBill;
import cn.edu.xmu.oomall.transaction.util.RefundBillObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

/**
 * @author 高艺桐 22920192204199
 * @date 2021/12/11 18:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundVo implements RefundBillObject {

    private String documentId;

    private Byte documentType;

    private Long paymentId;

    private String descr;

    private Long amount;

    private String reason;

    @Override
    public RefundBill createRefundBill() {
        return new RefundBill(paymentId, documentId, documentType, descr, amount, reason, null, null);
    }
}
