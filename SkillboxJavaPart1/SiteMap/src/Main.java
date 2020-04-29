import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static Scanner scanner;
    private static String url;
    private static long start;
    private static ForkJoinPool forkJoinPool;
    private static Thread infoThread;
    private static Thread workThread;
    private static boolean isWorkFinished = false;

    public static void main(String[] args) {

        start = System.currentTimeMillis();
        getUrl();
        System.out.println("\nАдрес успешно распознан");
        startWorkThread();
    }

    private static void getUrl() {
        System.out.print("Введите адрес сайта для создания карты в формате \"https://skillbox.ru\": ");
        while (!isUrlValid()) {
            System.out.print("\nОшибка в формате адреса сайта, проверьте и введите повторно: ");
        }
    }

    private static void startWorkThread() {
        workThread = new Thread(() -> {
            int numberOfThreads = setNumberOfThreads();
            if (numberOfThreads == 0) {
                forkJoinPool = new ForkJoinPool();
            } else {
                forkJoinPool = new ForkJoinPool(numberOfThreads);
            }
            System.out.println("\nСоздаем карту сайта...");
            startInfoThread();
            Page mainPage = forkJoinPool.invoke(new Parser(url));
            System.out.println("================================");
            isWorkFinished = true;
            System.out.println(mainPage.getPageLevel() + " " + mainPage.getUrl());
            mainPage.getSubPagesList().forEach(i -> System.out.println(i.getPageLevel() + " " + i.getUrl()));
            System.out.println("**********************\n");
            buildStringsFromPage(mainPage).forEach(System.out::print);
            System.out.println("Работа завершена " + (int) ((System.currentTimeMillis() - start) / 1000) + " c");
            if (isWriteToFile()) {
                writeStringToFile(buildStringsFromPage(mainPage));
            } else {
                System.out.println("Все задания завершены.");
            }
        });
        workThread.start();
    }

    private static void startInfoThread() {
        infoThread = new Thread(() -> {
            while (!isWorkFinished) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("\n****** " + " Активных потоков - " + forkJoinPool.getActiveThreadCount() +
                        ", размер пула - " + forkJoinPool.getPoolSize() + ", задач - " + forkJoinPool.getQueuedTaskCount());
            }
        });
        infoThread.start();
    }

    private static boolean isUrlValid() {
        scanner = new Scanner(System.in);
        url = scanner.nextLine().trim();
        if (url.matches("(http(s)*://)*(www\\.)*.+\\..+")) {
            String https = "https://";
            url = url.replaceAll("(http(s)*://)*(www\\.)*", "");
            url = https + url + "/";
            return true;
        }
        return false;
    }

    private static ArrayList<String> buildStringsFromPage(Page page) {
        final String tab = "\t";
        ArrayList<String> resultList = new ArrayList<>();
        // String urlString = page.getPageLevel() + " " + page.getUrl();
        String tabString = "";
        for (int j = 0; j < page.getPageLevel(); j++) {
            tabString += tab;
        }
        resultList.add(tabString + page.getUrl() + "\n"); // С urlString - отобразить уровни
        if (page.getSubPagesList() != null) {
            for (Page outPage : page.getSubPagesList()) {
                ArrayList<String> tempStringList = buildStringsFromPage(outPage);
                resultList.addAll(tempStringList);
            }
        }
        return resultList;
    }

    private static boolean isWriteToFile() {
        while (true) {
            System.out.println("Вы хотите сохранить результаты в файл? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine().toUpperCase().trim();
            if (answer.equals("Y") || answer.equals("YES")) {
                return true;
            } else if (answer.equals("N") || answer.equals("NO")) {
                return false;
            } else {
                System.out.println("Извините, ответ не распознан");
            }
        }
    }

    private static int setNumberOfThreads() {
        for (;;) {
            System.out.println("Введите количество создаваемых потоков (\"0\" - установит значение по умолчанию)");
            Scanner scanner = new Scanner(System.in);
            int answer = scanner.nextInt();
            if (answer == 0) {
                return 0;
            } else if (answer > 0) {
                return answer;
            } else {
                System.out.println("Извините, ответ не распознан");
            }
        }
    }

    private static void writeStringToFile(List<String> stringList) {
        try {
        String filePath = "data/results.txt";
        Files.deleteIfExists(Paths.get(filePath));
        Files.createFile(Path.of(filePath));
        File file = new File(filePath);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : stringList) {
                writer.write(s);
            }
            writer.close();
            System.out.println("Файл " + filePath + " записан.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}