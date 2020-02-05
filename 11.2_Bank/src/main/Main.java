package main;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static Bank bank;
    final static Double BIG_TRANSFERS_PERCENT = 0.05;
    final static Integer TRANSFERS_QUANTITY = 100000;
    final static Integer THREADS_QUANTITY = 8;
    final static Integer ACCOUNTS_QUANTITY = 10;
    static AtomicInteger transferCounter = new AtomicInteger();

    public static void main(String[] args) {
        System.out.println("Создаем Банк");
        bank = new Bank();
        // Создаем Банк. Генерируем случайные имена клиентов и создаем объекты main.Account со случаными
        // номерами счетов и количеством денег. Вместо имени клиента будет номер для заполнения HashMap
        System.out.println("Количество аккаунтов - " + ACCOUNTS_QUANTITY);
        for (int i = 0; i < ACCOUNTS_QUANTITY; i++) {
            String clientName = String.valueOf(i);
            String accountNumber = "40817" + (int) (Math.pow(Math.random(), Math.random() * 10) * 100000); // случ.номер
            Long initBalance = (long) (Math.pow(Math.random(), Math.random() * 10) * 10000000); // Случайный баланс
            Account account = new Account(initBalance, accountNumber);
            bank.addAccount(clientName, account);
        }
        System.out.println("Количество аккаунтов всего - " + bank.getAccounts().size());

        for (int i = 0; i < THREADS_QUANTITY; i++) {
            int finalI = i;
            new Thread(() -> {
                while (transferCounter.get() <= TRANSFERS_QUANTITY) {   // Цикл переводов (для каждого потока
                    transferCounter.incrementAndGet();
                    Account rndmClientFrom = getRandomAccount(); // Случайный клиент 1
                    Account rndmClientTo = getRandomAccount(); // Случайный клиент 2
                    while (rndmClientFrom.equals(rndmClientTo)) {
                        rndmClientTo = getRandomAccount();
                    }  // Случайая сумма перевода с учетом вероятнсти большой суммы
                    double random = Math.random();
                    Long transferAmount;
                    if (random < BIG_TRANSFERS_PERCENT) { // Сумма больше 50000, проверка СБ
                        transferAmount = 50000 + (long) (Math.random() * Math.random() * 1000000);
                    } else {
                        transferAmount = (long) (Math.random() * 50000); } // Сумма меньше 50000
                    // System.out.println("Поток " + finalI + " " + "Переведем " + transferCounter.get() + " cчета " +
                    // rndmClientFrom.getAccNumber() + " " + rndmClientTo.getAccNumber() + " " + transferAmount);
                    bank.transfer(rndmClientFrom.getAccNumber(), rndmClientTo.getAccNumber(), transferAmount);
                    System.out.println("Поток " + finalI + " " + "Операция перевода №" + transferCounter.get() + " "
                            + rndmClientFrom.getAccNumber() + " -> " + rndmClientTo.getAccNumber() + " - " + transferAmount);
                }
                // Поскольку счетов мало (10), а транзакций много, то есть вероятность что все счета будут заблокированы
                // Вероятность крупной транзакции 5%, тогда вероятность блокировки 2,5% (то есть для каждого счета
                // достаточно в среднем 40 транзакций чтобы попасть в блокировку
                // для последовательной блокировки всех счетов в среднем достаточно 40 * 10 = 400 транзакций
                System.out.println("Список заблокированных счетов:");
                bank.getAccounts().values().forEach(j -> {
                    if (j.isBlocked()) {
                        System.out.print(j.getAccNumber() + " ");
                    }
                });
                System.out.println("\n");

            }).start();
        }
        System.out.println("==== Парам пам");
    }

    private static Account getRandomAccount() {
        Account account = null;
        int keyCount = (int) (Math.random() * ACCOUNTS_QUANTITY);
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
