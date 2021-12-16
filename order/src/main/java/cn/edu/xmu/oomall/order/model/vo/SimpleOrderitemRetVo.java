package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderitemRetVo {
    private Long productId;
    private String name;
    private Long quantity;
    private Long price;
}
