package project.transactionlog;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "transaction-log",
        info = @Info(
                title = "Transaction Log Contract"
        )
)
@Default
public final class TransactionLogContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum TransactionLogErrors {
        TRANSACTION_ALREADY_EXISTS,
        TRANSACTION_NOT_FOUND,
        INVALID_INPUT
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        CreateTransaction(
                ctx,
                "init-001",
                "2026-01-01T10:00:00Z",
                "Alice",
                "Piero",
                "100.00",
                "PAYMENT"
        );

        CreateTransaction(
                ctx,
                "init-002",
                "2026-01-01T10:01:00Z",
                "Piero",
                "Franco",
                "50.00",
                "TRANSFER"
        );

        CreateTransaction(
                ctx,
                "init-003",
                "2026-01-01T10:02:00Z",
                "Franco",
                "Alice",
                "25.00",
                "REFUND"
        );
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public TransactionRecord CreateTransaction(
            final Context ctx,
            final String transactionId,
            final String timestamp,
            final String sender,
            final String receiver,
            final String amount,
            final String transactionType) {

        validateRequired(transactionId, "transactionId");
        validateRequired(timestamp, "timestamp");
        validateRequired(sender, "sender");
        validateRequired(receiver, "receiver");
        validateRequired(amount, "amount");
        validateRequired(transactionType, "transactionType");

        if (TransactionExists(ctx, transactionId)) {
            String errorMessage = String.format("Transaction %s already exists", transactionId);
            throw new ChaincodeException(errorMessage, TransactionLogErrors.TRANSACTION_ALREADY_EXISTS.toString());
        }
        TransactionRecord transactionRecord = new TransactionRecord(transactionId, timestamp, sender, receiver, amount, transactionType);
        String transactionJson = genson.serialize(transactionRecord);
        ctx.getStub().putStringState(transactionId, transactionJson);
        return transactionRecord;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public TransactionRecord ReadTransaction(final Context ctx, final String transactionId) {
        validateRequired(transactionId, "transactionId");
        String transactionJson = ctx.getStub().getStringState(transactionId);

        if (!TransactionExists(ctx, transactionId)) {
            String errorMessage = String.format("Transaction %s does not exist", transactionId);
            throw new ChaincodeException(errorMessage, TransactionLogErrors.TRANSACTION_NOT_FOUND.toString());
        }
        return genson.deserialize(transactionJson, TransactionRecord.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public TransactionRecord UpdateTransaction(
            final Context ctx,
            final String transactionId,
            final String timestamp,
            final String sender,
            final String receiver,
            final String amount,
            final String transactionType) {

        validateRequired(transactionId, "transactionId");
        validateRequired(timestamp, "timestamp");
        validateRequired(sender, "sender");
        validateRequired(receiver, "receiver");
        validateRequired(amount, "amount");
        validateRequired(transactionType, "transactionType");

        if (!TransactionExists(ctx, transactionId)) {
            String errorMessage = String.format("Transaction %s does not exist", transactionId);
            throw new ChaincodeException(errorMessage, TransactionLogErrors.TRANSACTION_NOT_FOUND.toString());
        }
        TransactionRecord updatedTransaction = new TransactionRecord(transactionId, timestamp, sender, receiver, amount, transactionType);
        String transactionJson = genson.serialize(updatedTransaction);
        ctx.getStub().putStringState(transactionId, transactionJson);
        return updatedTransaction;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteTransaction(final Context ctx, final String transactionId) {
        validateRequired(transactionId, "transactionId");
        if (!TransactionExists(ctx, transactionId)) {
            String errorMessage = String.format("Transaction %s does not exist", transactionId);
            throw new ChaincodeException(errorMessage, TransactionLogErrors.TRANSACTION_NOT_FOUND.toString());
        }
        ctx.getStub().delState(transactionId);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean TransactionExists(
            final Context ctx,
            final String transactionId) {

        validateRequired(transactionId, "transactionId");
        String transactionJson = ctx.getStub().getStringState(transactionId);
        return transactionJson != null && !transactionJson.isEmpty();
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllTransactions(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<TransactionRecord> transactions = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            TransactionRecord transactionRecord = genson.deserialize(result.getStringValue(), TransactionRecord.class);
            transactions.add(transactionRecord);
        }

        return genson.serialize(transactions);
    }

    private void validateRequired(final String value, final String fieldName) {

        if (value == null || value.trim().isEmpty()) {
            String errorMessage = String.format("Field %s is required", fieldName);
            throw new ChaincodeException(errorMessage, TransactionLogErrors.INVALID_INPUT.toString());
        }
    }
}
