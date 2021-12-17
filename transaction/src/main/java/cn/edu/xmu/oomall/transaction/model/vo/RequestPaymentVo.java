package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.util.PaymentBill;
import cn.edu.xmu.oomall.transaction.util.PaymentBillObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestPaymentVo implements PaymentBillObject {

    @NotNull
    private Long patternId;

    @NotNull
    private String documentId;

    @NotNull
    private Byte documentType;

    @NotNull
    private String descr;

    @NotNull
    private Long amount;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime beginTime;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endTime;

    @Override
    public PaymentBill createPaymentBill() {
        return new PaymentBill(patternId, documentId, documentType, descr, amount, beginTime, endTime);
    }
}
