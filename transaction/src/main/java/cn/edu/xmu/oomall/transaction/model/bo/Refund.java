package cn.edu.xmu.oomall.transaction.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund implements Serializable {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long paymentId;
    private Long amount;
    private Byte state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime refundTime;

    private String documentId;

    private Byte documentType;

    private String descr;

    private Long adjustId;

    private String adjustName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime adjustTime;

    private Long creatorId;

    private String creatorName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime gmtCreate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime gmtModified;

    private Long modifierId;

    private String modifierName;
}
