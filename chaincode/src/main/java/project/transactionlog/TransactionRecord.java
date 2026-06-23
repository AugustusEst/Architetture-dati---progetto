package project.transactionlog;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class TransactionRecord {

    @Property()
    private final String transactionId;

    @Property()
    private final String timestamp;

    @Property()
    private final String sender;

    @Property()
    private final String receiver;

    @Property()
    private final String amount;

    @Property()
    private final String transactionType;

    public String getTransactionId() {
        return transactionId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public TransactionRecord(
            @JsonProperty("transactionId") final String transactionId,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("sender") final String sender,
            @JsonProperty("receiver") final String receiver,
            @JsonProperty("amount") final String amount,
            @JsonProperty("transactionType") final String transactionType) {

        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TransactionRecord)) {
            return false;
        }
        TransactionRecord other = (TransactionRecord) obj;
        return Objects.equals(transactionId, other.transactionId)
                && Objects.equals(timestamp, other.timestamp)
                && Objects.equals(sender, other.sender)
                && Objects.equals(receiver, other.receiver)
                && Objects.equals(amount, other.amount)
                && Objects.equals(transactionType, other.transactionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, timestamp, sender, receiver, amount, transactionType);
    }

    @Override
    public String toString() {
        return "TransactionRecord{"
                + "transactionId='" + transactionId + '\''
                + ", timestamp='" + timestamp + '\''
                + ", sender='" + sender + '\''
                + ", receiver='" + receiver + '\''
                + ", amount='" + amount + '\''
                + ", transactionType='" + transactionType + '\''
                + '}';
    }
}