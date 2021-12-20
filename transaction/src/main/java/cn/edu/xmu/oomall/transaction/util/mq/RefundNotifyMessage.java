package cn.edu.xmu.oomall.transaction.util.mq;

import cn.edu.xmu.oomall.transaction.model.bo.RefundState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundNotifyMessage {

    private Byte documentType;

    private String documentId;

    private RefundState refundState;

}
