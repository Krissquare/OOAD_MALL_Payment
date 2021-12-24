package cn.edu.xmu.oomall.ordermq.service.mq.vo;

import cn.edu.xmu.oomall.ordermq.service.mq.bo.RefundState;
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
