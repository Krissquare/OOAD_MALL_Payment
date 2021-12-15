package cn.edu.xmu.oomall.transaction.util;


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

    private MessageType messageType;

    public enum MessageType {
        PAYMENT((byte) 1, "PaymentMessage"),
        REFUND((byte) 2, "RefundMessage");

        private byte code;

        private String description;

        MessageType(byte code, String description) {
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
