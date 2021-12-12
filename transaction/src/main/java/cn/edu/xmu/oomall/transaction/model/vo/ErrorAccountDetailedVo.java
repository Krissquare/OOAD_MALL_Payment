package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.model.po.ErrorAccountPo;
import cn.edu.xmu.privilegegateway.annotation.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorAccountDetailedVo {
    Long id;
    String tradeSn;
    Long patternId;
    Long income;
    Long expenditure;
    Byte state;
    String documentId;
    String descr;
    LocalDateTime time;
    SimpleVo adjust;
    LocalDateTime adjustTime;
    SimpleVo creator;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    SimpleVo modifier;

    public static ErrorAccountDetailedVo generateFromErrorAccountPo(ErrorAccountPo errorAccountPo){
        ErrorAccountDetailedVo tar = Common.cloneVo(errorAccountPo, ErrorAccountDetailedVo.class);
        tar.setAdjust(new SimpleVo(errorAccountPo.getAdjustId(), errorAccountPo.getAdjustName()));
        tar.setCreator(new SimpleVo(errorAccountPo.getCreatorId(), errorAccountPo.getCreatorName()));
        tar.setModifier(new SimpleVo(errorAccountPo.getModifierId(), errorAccountPo.getModifierName()));
        return tar;
    }

}
