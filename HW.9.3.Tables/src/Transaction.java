import java.time.LocalDate;

public class Transaction {

    private String accountNumber;
    private Currency currency;
    private LocalDate date;
    private long income;
    private long expense;
    private String contractor;
    private String mccCode;


    public Transaction(String accountNumber, Currency currency, LocalDate date, long income, long expense, String contractor, String mccCode) {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.date = date;
        this.income = income;
        this.expense = expense;
        this.contractor = contractor;
        this.mccCode = mccCode;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getIncome() {
        return income;
    }

    public void setIncome(long income) {
        this.income = income;
    }

    public long getExpense() {
        return expense;
    }

    public void setExpense(long expense) {
        this.expense = expense;
    }

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getMccCode() {
        return mccCode;
    }

    public void setMccCode(String mccCode) {
        this.mccCode = mccCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}