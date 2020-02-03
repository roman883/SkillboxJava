package main;

import java.util.HashMap;

public class Main {

    static int clientsCount, threadsCount, accountsCount;
    static Bank bank;

    public static void main(String[] args) {
        System.out.println("Создаем Банк");
        bank = new Bank();
        // Создаем Банк. Генерируем случайные имена клиентов и создаем объекты main.Account со случаными
        // номерами счетов и количеством денег. Вместо имени клиента будет номер для заполнения HashMap
        clientsCount = (int) (Math.random() * 10000);
        System.out.println("Количество клиентов - " + clientsCount);
        for (int i = 0; i < clientsCount; i++) {
            String clientName = String.valueOf(i);
            System.out.println("Создаем клиента " + clientName);
            String accountNumber = "40817" + (int) (Math.pow(Math.random(), Math.random() * 10) * 100000); // случ.номер
            System.out.println("Создаем аккаунт номер " + accountNumber);
            Long initBalance = (long) (Math.pow(Math.random(), Math.random() * 10) * 10000000); // Случайный баланс
            System.out.println("Баланс стартовый для аккаунта " + initBalance);
            Account account = new Account(initBalance, accountNumber);
            bank.addAccount(clientName, account);
            System.out.println("Аккаунт создан и добавлен");
        }
        System.out.println("Все аккаунты добавлены");
        // Создаем до 1000 одновременных потоков (обращений клиентов) с переводами денег друг другу - случайное
        // количество от 0 до 52 500 (2500 - 5% случаев превышения 50000)

        threadsCount = (int) ((Math.random() * 1000) + 50);
        System.out.println("Количество потоков - " + threadsCount);
        accountsCount = bank.getAccounts().size();
        System.out.println("Количество аккаунтов всего - " + accountsCount);

        for (int i = 0; i < threadsCount; i++) {
            new Thread(() -> {
                    Account rndmClientFrom = getRandomAccount(); // Случайный клиент 1
                    Account rndmClientTo = getRandomAccount(); // Случайный клиент 2
                while (rndmClientFrom == rndmClientTo) {
                    rndmClientTo = getRandomAccount();
                }
                Long transferAmount = (long) (Math.random() * 52500);  // Случайный перевод от 0 до 52500. Вероятность суммы 5%
//                System.out.println("Переводим " + transferAmount);
               bank.transfer(rndmClientFrom.getAccNumber(), rndmClientTo.getAccNumber(), transferAmount);
            }).start();
        }
    }

    private static Account getRandomAccount() {
        Account account = null;
        int keyCount = (int) (Math.random() * accountsCount);
        int count = 0;
        for (HashMap.Entry<String, Account> entry : bank.getAccounts().entrySet()) {
            if (count == keyCount) {
                account = entry.getValue();
                break;
            } else {
                count++;
            }
        }
        return account;
    }
}
