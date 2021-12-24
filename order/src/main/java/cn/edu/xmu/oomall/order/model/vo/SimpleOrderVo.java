package cn.edu.xmu.oomall.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 15:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleOrderVo {
    @NotNull
    @Valid
    private List<SimpleOrderItemVo> orderItems;
    @NotNull
    @NotBlank
    private String consignee;
    @NotNull
    private Long regionId;
    private String address;
    @Pattern(regexp = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$", message = "电话号码不符合格式")
    private String mobile;
    private String message;
    private Long advancesaleId;
    private Long grouponId;
    private Long customerId;

    @NotNull
    private Long point;
}
