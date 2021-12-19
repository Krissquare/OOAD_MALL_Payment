package cn.edu.xmu.oomall.transaction.util.mq;

import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentQueryMessage {

    private PaymentBill paymentBill;


}
