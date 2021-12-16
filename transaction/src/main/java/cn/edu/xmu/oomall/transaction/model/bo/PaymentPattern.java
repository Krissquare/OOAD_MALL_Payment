package cn.edu.xmu.oomall.transaction.model.bo;

import lombok.Data;

import java.io.Serializable;


@Data
public class PaymentPattern implements Serializable {
    private Long id;

    private Byte state;

    private String className;
}
