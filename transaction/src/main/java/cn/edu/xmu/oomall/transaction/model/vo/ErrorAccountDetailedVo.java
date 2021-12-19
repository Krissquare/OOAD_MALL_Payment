package cn.edu.xmu.oomall.transaction.model.vo;

import cn.edu.xmu.oomall.transaction.model.po.ErrorAccountPo;
import cn.edu.xmu.privilegegateway.annotation.util.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime time;
    SimpleVo adjust;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime adjustTime;
    SimpleVo creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime gmtModified;
    SimpleVo modifier;

    public static ErrorAccountDetailedVo generateFromErrorAccountPo(ErrorAccountPo errorAccountPo){
        ErrorAccountDetailedVo tar = Common.cloneVo(errorAccountPo, ErrorAccountDetailedVo.class);
        tar.setAdjust(new SimpleVo(errorAccountPo.getAdjustId(), errorAccountPo.getAdjustName()));
        tar.setCreator(new SimpleVo(errorAccountPo.getCreatorId(), errorAccountPo.getCreatorName()));
        tar.setModifier(new SimpleVo(errorAccountPo.getModifierId(), errorAccountPo.getModifierName()));
        return tar;
    }

}
