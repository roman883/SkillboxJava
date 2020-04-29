import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public final class TransactionParser {

    private String dataFilePath;

    public TransactionParser(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public TransactionParseResult parseTheFile() {
        TransactionParseResult transactionParseResult;
        List<Transaction> transactions = new ArrayList<>();
        List<String> notValidLines = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFilePath));
            for (String line : lines) {
                String currencyString = "";
                String accountNumber = "";
                Currency currency = Currency.RUR;
                LocalDate date = LocalDate.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
                String transactionContractor = "";
                long income = 0;
                long expense = 0;
                String mccCode = "";
                final int INDEX_ACCOUNT = 1;
                final int INDEX_CURRENCY = 0;
                final int INDEX_DATE = 3;
                final int INDEX_TRANSACTION_CONTRACTOR = 1;
                final int INDEX_INCOME_STRING = 2;
                final int INDEX_INCOME = 0;
                final int CURRENCY_MULTIPLIER = 100;
                final int INDEX_MCC = 1;
                if (line.matches(".+\\d+.+")) {
                    accountNumber = line.split(",")[INDEX_ACCOUNT];
                    currencyString = line.split("\\s{4,}", 3)[INDEX_INCOME_STRING]
                            .replaceAll("(\\d\\d\\.\\d\\d\\.\\d\\d\\s+){2}", "")
                            .replaceAll("(\\d+\\.\\d\\d\\s+)", "").trim()
                            .split("\\s+")[INDEX_CURRENCY].trim();
                    if (currencyString.toUpperCase().equals("RUR")) {
                        currency = Currency.RUR;
                    } else if (currencyString.toUpperCase().equals("USD")) {
                        currency = Currency.USD;
                    } else if (currencyString.toUpperCase().equals("EUR")) {
                        currency = Currency.EUR;
                    } else {
                        System.out.println("Валюта в следующей строке не распознана. Используем по умолчанию рубли.\n" + line + "-------");
                    }
                    date = LocalDate.parse(line.split(",")[INDEX_DATE], dateTimeFormatter);
                    transactionContractor = line.split("\\s{4,}", 3)[INDEX_TRANSACTION_CONTRACTOR]
                            .replaceAll(".+[\\\\]", "")
                            .replaceAll(".+[/]", "").trim();
                    String incomeOrExpenseString = line.split("\\s{4,}", 3)[INDEX_INCOME_STRING]
                            .replaceAll("\\d\\d\\.\\d\\d\\.\\d\\d\\s+\\d\\d\\.\\d\\d\\.\\d\\d", "")
                            .split("MCC")[INDEX_INCOME].replaceAll("\\s+[A-Z]+.+", "");
                    final long INCOME_OR_EXPENSE_SUM = (long) (CURRENCY_MULTIPLIER * Double.parseDouble(incomeOrExpenseString.replaceAll("\\s+[A-Z]+.+", "").trim()));
                    if (incomeOrExpenseString.matches("\\s{4,}.+")) {
                        expense = INCOME_OR_EXPENSE_SUM;
                    } else {
                        income = INCOME_OR_EXPENSE_SUM;
                    }
                    mccCode = line.split("\\s{4,}", 3)[INDEX_INCOME_STRING]
                            .replaceAll("\\d\\d\\.\\d\\d\\.\\d\\d\\s+\\d\\d\\.\\d\\d\\.\\d\\d", "")
                            .split("MCC")[INDEX_MCC].substring(0, 4); // или .split(",")[0] - т.к. идет разделение по запятой, также можно добавить MCC перед цифрами
                    try {
                        transactions.add(new Transaction(accountNumber, currency, date, income, expense, transactionContractor, mccCode));
                    } catch (Exception ex) {
                        notValidLines.add(line);
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        transactionParseResult = new TransactionParseResult(transactions, notValidLines);
        return transactionParseResult;
    }
}