package cn.edu.xmu.oomall.transaction.model.bo;

import lombok.Data;


@Data
public class PaymentPattern {
    private Long id;

    private Byte state;

    private String className;
}
