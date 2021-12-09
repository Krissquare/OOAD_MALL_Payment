package cn.edu.xmu.oomall.ooad201.payment.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RefundRecVo {
    @NotNull
    private Byte state;
    @NotBlank
    private String descr;
}
