package main;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bank {
    private ConcurrentHashMap<String, Account> accounts;
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
    public void transfer(String fromAccountNum, String toAccountNum, long amount) {
        boolean hasToBlock = false;
        Account fromAcc = getAccount(fromAccountNum);
        Account toAcc = getAccount(toAccountNum);
        try {
            while (fromAcc.getMutex().availablePermits() < 1) {
                Thread.sleep(700);
            }
            fromAcc.getMutex().acquire();
            while (toAcc.getMutex().availablePermits() < 1) {
                fromAcc.getMutex().release();
                Thread.sleep(1200);
                fromAcc.getMutex().acquire();
            }
            toAcc.getMutex().acquire();
            {
                if (isBlocked(fromAcc) || isBlocked(toAcc)) {
                    System.out.println("Операция невозможна, счет(а) заблокирован(ы)");
                } else {
                    if ((fromAcc.getMoney() >= amount) && (toAcc != null)) { // Проверка достаточности денег для перевода и наличия в списпке аккаунта получателя
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
                if (hasToBlock) {
                    blockAccounts(fromAcc, toAcc);
                } else if (!hasToBlock && (amount > 50000)){
                    System.out.println("Транзакция УСПЕШНО прошла проверку службы безопасности!");
                }
            }
            toAcc.getMutex().release();
            fromAcc.getMutex().release();
        } catch (InterruptedException ex) {
            System.out.println("Получили interrupted exception");
            ex.printStackTrace();
        }
    }

    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     */
    public Long getBalance(String accountNum) {
        if (getAccount(accountNum) != null) {
            return getAccount(accountNum).getMoney();
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

    public Account getAccount(String accountNum) {
        for (Account account : accounts.values()) {
            if (account.getAccNumber().equals(accountNum)) {
                return account;
            }
        }
        System.out.println("Аккаунт в базе не найден");
        return null;
    }

    public Bank(ConcurrentHashMap<String, Account> accounts) {
        this.accounts = accounts;
    }

    public Bank() {
        accounts = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ConcurrentHashMap<String, Account> accounts) {
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


