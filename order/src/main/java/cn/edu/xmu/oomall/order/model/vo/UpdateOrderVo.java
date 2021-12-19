package cn.edu.xmu.oomall.order.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Fang Zheng
 * */
@Data
@NoArgsConstructor
public class UpdateOrderVo {
    String consignee;
    Long regionId;
    String address;
    @Pattern(regexp = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$",
             message = "电话号码不符合格式")
    String mobile;
}
