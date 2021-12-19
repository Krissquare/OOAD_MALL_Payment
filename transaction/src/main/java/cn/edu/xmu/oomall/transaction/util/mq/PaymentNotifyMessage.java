package cn.edu.xmu.oomall.transaction.util.mq;


import cn.edu.xmu.oomall.transaction.model.bo.PaymentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotifyMessage {

    private Byte documentType;

    private String documentId;

    private PaymentState paymentState;

}
