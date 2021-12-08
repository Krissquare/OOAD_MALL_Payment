package cn.edu.xmu.oomall.ooad201.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVo {
    @NotBlank(message = "留言不能为空")
    private String message;
}
