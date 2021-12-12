package cn.edu.xmu.oomall.transaction.util.alipay;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.TransactionPattern;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AlipayTransaction extends TransactionPattern {


    @Override
    public ReturnObject requestPayment(Long requestNo, PaymentBill bill) {
        return null;
    }
}
