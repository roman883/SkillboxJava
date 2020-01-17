import java.util.List;

public class TransactionParseResult {

    private List<Transaction> transactions;
    private List<String> notValidLines;

    public TransactionParseResult(List<Transaction> transactions, List<String> notValidLines) {
        this.transactions = transactions;
        this.notValidLines = notValidLines;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<String> getNotValidLines() {
        return notValidLines;
    }

    public void setNotValidLines(List<String> notValidLines) {
        this.notValidLines = notValidLines;
    }
}