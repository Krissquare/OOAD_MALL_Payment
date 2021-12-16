package cn.edu.xmu.oomall.transaction.util.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveQueryMessage {

    private Long patternId;

    private String outTradeNo;

    private String requestNo;

    private QueryMessageType messageType;

    private Object bill;

    public enum QueryMessageType {
        QUERY_PAYMENT((byte) 1, "QueryPaymentMessage"),
        QUERT_REFUND((byte) 2, "QueryRefundMessage");

        private byte code;

        private String description;

        QueryMessageType(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public byte getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
