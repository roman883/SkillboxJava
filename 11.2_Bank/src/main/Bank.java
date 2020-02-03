package main;

import java.util.HashMap;
import java.util.Random;

public class Bank {
    private HashMap<String, Account> accounts;
    private final Random random = new Random();

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    /**
     * TODO: реализовать метод. Метод переводит деньги между счетами.
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка
     * счетов (как – на ваше усмотрение)
     */
    public synchronized void transfer(String fromAccountNum, String toAccountNum, long amount) {
        // Проверка блокировки аккаунтов
        boolean hasToBlock = false;
        Account fromAcc = findAccount(fromAccountNum);
        Account toAcc = findAccount(toAccountNum);
        try {
            if (isBlocked(fromAcc) || isBlocked(toAcc)) {
                System.out.println("Операция невозможна, счет(а) заблокирован(ы)");
            } else {
                // Проверка достаточности денег для перевода и наличия в списпке аккаунта получателя
                if ((fromAcc.getMoney() >= amount) && (toAcc != null)) {
                    // Переводим
                    Long tempBalance = fromAcc.getMoney();
                    tempBalance -= amount;
                    fromAcc.setMoney(tempBalance);
                    tempBalance = toAcc.getMoney() + amount;
                    toAcc.setMoney(tempBalance);
                    if (amount > 50000) {
                        hasToBlock = isFraud(fromAccountNum, toAccountNum, amount);
                    }
                } else {
                    System.out.println("Невозможно осуществить перевод. Проверьте счета и достаточность средств");
                }
            }
        } catch (
                InterruptedException ex) {
            System.out.println("Получили interrupted exception");
            ex.printStackTrace();
        }
        if (hasToBlock) {
            blockAccounts(fromAcc, toAcc);
        }
        else {
            System.out.println("Транзакция УСПЕШНО прошла проверку службы безопасности!");
        }
    }


    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     */
    public Long getBalance(String accountNum) {
        if (findAccount(accountNum) != null) {
            return findAccount(accountNum).getMoney();
        } else {
            return null;
        }
    }

    public Boolean isBlocked(Account accNumber) {
        return accNumber.isBlocked();
    }

    public void blockAccounts(Account fromAccountNumber, Account toAccountNumber) {
        fromAccountNumber.setBlocked(true);
        toAccountNumber.setBlocked(true);
        System.out.println("Следующие счета заблокированы: " + fromAccountNumber.getAccNumber() + " " + toAccountNumber.getAccNumber());
    }

    public Account findAccount(String accountNum) {
        for (Account account : accounts.values()) {
            if (account.getAccNumber().equals(accountNum)) {
                return account;
            }
        }
        System.out.println("Аккаунт в базе не найден");
        return null;
    }

    public Bank(HashMap<String, Account> accounts) {
        this.accounts = accounts;
    }

    public Bank() {
        accounts = new HashMap<>();
    }

    public HashMap<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(HashMap<String, Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(String name, Account account) {
        accounts.put(name, account);
    }

    public void deleteAccount(String name) {
        accounts.remove(name);
    }

    public void deleteAccount(String name, Account account) {
        accounts.remove(name, account);
    }
}


