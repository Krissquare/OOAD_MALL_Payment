package cn.edu.xmu.oomall.transaction.util.mq;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyMessage {

    private String documentId;

    private Byte documentType;

    private Byte state;

    private NotifyMessageType messageType;

    public enum NotifyMessageType {
        PAYMENT((byte) 1, "PaymentMessage"),
        REFUND((byte) 2, "RefundMessage");

        private byte code;

        private String description;

        NotifyMessageType(byte code, String description) {
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
