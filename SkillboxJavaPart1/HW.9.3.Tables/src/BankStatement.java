public class BankStatement {

    private static String dataFile = "data/movementList.csv";

    public static void main(String[] args) {

        System.out.println("Найден файл с данными: " + dataFile + "\nПарсим...");
        TransactionParser transactionParser = new TransactionParser(dataFile);
        TransactionParseResult transactionParseResult;
        transactionParseResult = transactionParser.parseTheFile();
        System.out.println(TransactionAnalyze.calculateIncome(transactionParseResult));
        System.out.println(TransactionAnalyze.calculateExpenses(transactionParseResult));
        System.out.println("Детализированные расходы:");
        TransactionAnalyze.getDetailedOutcome(transactionParseResult).forEach(System.out::println);
    }
}
