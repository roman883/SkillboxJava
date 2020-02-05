package test;

import junit.framework.TestCase;
import main.Account;
import main.Bank;

public class BankTest extends TestCase {

    Bank bank;

    protected void setUp() throws Exception {

        bank = new Bank();
        // Создаем Банк. Генерируем случайные имена клиентов и создаем объекты main.Account со случаными
        // номерами счетов и количеством денег. Вместо имени клиента будет номер для заполнения HashMap
        int clientsCount = (int) Math.random() * 10000;
        for (int i = 0; i < clientsCount; i++) {
            String clientName = String.valueOf(i);
            String accountNamber = "40817" + (int) (Math.pow(Math.random(), Math.random() * 10) * 100000); // случ.номер
            Long initBalance = (long) (Math.pow(Math.random(), Math.random() * 10) * 10000000); // Случайный баланс
            Account account = new Account(initBalance, accountNamber);
            bank.addAccount(clientName, account);
        }
        // Заданные аккаунты для теста
        String clientIvanov = "Ivanov";
        String clientPetrov = "Petrov";
        String accountNamberIvanov = "4081712564567";
        String accountNumberPetrov = "4081765644648";
        Long initBalanceIvanov = 100000L;
        Long initBalancePetrov = 70000L;
        bank.addAccount(clientIvanov, new Account(initBalanceIvanov, accountNamberIvanov));
        bank.addAccount(clientPetrov, new Account(initBalancePetrov, accountNumberPetrov));
    }

    public void testgetBalance() {
        Long actual = bank.getBalance("4081765644648");
        Long expected = 70000L;
        assertEquals(expected, actual);
    }

    public void testTransfer() {  // Тест перевода без службы безопасности
        bank.transfer("4081712564567", "4081765644648", 18000);
        Long expected = 82000L;
        Long actual = bank.getBalance("4081712564567");
        assertEquals(expected, actual);
    }


    public void testTransferBlocked() { // Тест переводом с направлением в службу безопасности
        bank.transfer("4081765644648", "4081712564567", 62000);
        Account petrovAccount = bank.getAccount("4081712564567");
        Account ivanovAccount = bank.getAccount("4081765644648");
        if (!bank.getAccount("4081712564567").isBlocked()) {
            bank.blockAccounts(ivanovAccount, petrovAccount);
        }
        Boolean expected = true;
        Boolean actual = bank.getAccount("4081765644648").isBlocked();
        assertEquals(expected, actual);
    }

    public void testBlockAccounts() {
        Account petrovAccount = bank.getAccount("4081712564567");
        Account ivanovAccount = bank.getAccount("4081765644648");
        bank.blockAccounts(ivanovAccount, petrovAccount);
        Boolean expected = true;
        Boolean actual = bank.getAccount("4081712564567").isBlocked();
        assertEquals(expected, actual);
    }
}
