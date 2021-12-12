package cn.edu.xmu.oomall.transaction.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/09 21:14
 */
@Data
public class NotifyRetVo {
    String code;
    String message;
    public NotifyRetVo(){
        this.code="success";
        this.message="成功";
    }
}
