import java.util.ArrayList;
import java.util.List;

public class TransactionAnalyze {

    public static String calculateIncome(TransactionParseResult transactionParseResult) {
        List<Transaction> transactionList = transactionParseResult.getTransactions();
        long incomeRUR = 0;
        long incomeUSD = 0;
        long incomeEUR = 0;
        final double CURRENCY_MULTIPLIER = 0.01;
        for (Transaction t : transactionList) {
            if (t.getCurrency().equals(Currency.RUR)) {
                incomeRUR += t.getIncome();
            } else if (t.getCurrency().equals(Currency.USD)) {
                incomeUSD += t.getIncome();
            } else {
                incomeEUR += t.getIncome();
            }
        }
        return "Сумма доходов: " + CURRENCY_MULTIPLIER * incomeRUR + " руб, " + CURRENCY_MULTIPLIER * incomeUSD
                + " usd, " + CURRENCY_MULTIPLIER * incomeEUR + " eur";
    }

    public static String calculateExpenses(TransactionParseResult transactionParseResult) {
        List<Transaction> transactionList = transactionParseResult.getTransactions();
        long expensesRUR = 0;
        long expensesUSD = 0;
        long expensesEUR = 0;
        final double CURRENCY_MULTIPLIER = 0.01;
        for (Transaction t : transactionList) {
            if (t.getCurrency().equals(Currency.RUR)) {
                expensesRUR += t.getExpense();
            } else if (t.getCurrency().equals(Currency.USD)) {
                expensesUSD += t.getExpense();
            } else {
                expensesEUR += t.getExpense();
            }
        }
        return "Сумма расходов: " + CURRENCY_MULTIPLIER * expensesRUR + " руб, " + CURRENCY_MULTIPLIER * expensesUSD
                + " usd, " + CURRENCY_MULTIPLIER * expensesEUR + " eur";
    }

    public static ArrayList<String> getDetailedOutcome(TransactionParseResult transactionParseResult) {
        List<Transaction> transactionList = transactionParseResult.getTransactions();
        ArrayList<String> resultList = new ArrayList<>();
        ArrayList<Long> expensesList = new ArrayList<>();
        ArrayList<String> contractorList = new ArrayList<>();
        ArrayList<Currency> currencyList = new ArrayList<>();
        final int CURRENCY_MULTIPLIER = 100;
        for (Transaction t : transactionList) {
            if (t.getExpense() > 0) {
                if (contractorList.isEmpty()) {
                    contractorList.add(t.getContractor());
                    expensesList.add(t.getExpense());
                    currencyList.add(t.getCurrency());
                } else {
                    int index = contractorList.indexOf(t.getContractor());
                    if (index == -1) {
                        contractorList.add(t.getContractor());
                        expensesList.add(t.getExpense());
                        currencyList.add(t.getCurrency());
                    } else if (currencyList.get(index).equals(t.getCurrency())) {
                        expensesList.add(index, (expensesList.get(index) + t.getExpense()));
                        expensesList.remove(index + 1);
                    } else if (!currencyList.get(index).equals(t.getCurrency())) {
                        contractorList.add(t.getContractor());
                        expensesList.add(t.getExpense());
                        currencyList.add(t.getCurrency());
                    }
                }
            }
        }
        for (int i = 0; i < contractorList.size(); i++) {
            resultList.add(contractorList.get(i) + "\t" + expensesList.get(i) / CURRENCY_MULTIPLIER + "\t" + currencyList.get(i));
        }
        return resultList;
    }
}