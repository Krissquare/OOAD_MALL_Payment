package cn.edu.xmu.oomall.order.service.mq.vo;

import cn.edu.xmu.oomall.order.microservice.bo.RefundState;
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
