package cn.edu.xmu.oomall.ordermq.service.mq.vo;


import cn.edu.xmu.oomall.ordermq.service.mq.bo.PaymentState;
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
