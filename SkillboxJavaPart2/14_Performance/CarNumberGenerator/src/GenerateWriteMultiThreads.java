import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Генерация номеров в несколько потоков, размер пула задается, при генерации используется StringBuilder в качестве
 * буфера. при достижении заданного размера, из буфера данные помещаются в очередь на запись. В процессе наполнения
 * очереди на запись, идет запись в один файл в два потока
 * 2 потока для генерации и 2 потока для записи задано исходя из 4 ядер процессора
 */

public class GenerateWriteMultiThreads {

    private static final int WRITE_THREADS_COUNT = 2;
    private static final int GENERATE_THREADS_COUNT = Runtime.getRuntime().availableProcessors() - WRITE_THREADS_COUNT;
    private static final int REGIONS_COUNT = 100;
    private static final String RESULT_FILE_OUTPUT = "res/numbers.threads.txt"; // Задал другой путь, т.к. удобнее
    private static final String TIMESTAMP_FILE = "res/time.txt";

    private static ThreadPoolExecutor threadPool;
    private static Queue<Integer> regionCodeQueue;
    private static Queue<StringBuilder> stringQueue;
    private static Queue<Boolean> regionsFinished = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            regionCodeQueue = new ConcurrentLinkedQueue<>(); // Очередь регионов
            stringQueue = new ConcurrentLinkedQueue<>(); // Очередь текстовых данных
            FileOutputStream timeWriter = new FileOutputStream(TIMESTAMP_FILE);
            threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(GENERATE_THREADS_COUNT);
            for (int j = 1; j < REGIONS_COUNT; j++) { // Наполняем регионами
                regionCodeQueue.add(j);
            }
            while (regionCodeQueue.peek() != null) { // Запускаем задачи для каждого региона
                int regionCode = regionCodeQueue.poll();
                Generator generator = new Generator(regionCode, stringQueue, regionsFinished);
                threadPool.execute(generator);
            }
            writeTheFile(); // Пишем файл
            threadPool.shutdown();
            if (threadPool.isShutdown()) { // выводим время, если все задачи завершены
                long measuredTime = (System.currentTimeMillis() - start);
                System.out.println(measuredTime + " ms");
                StringBuilder result = new StringBuilder("Измеренное время с PrintWriter, ").append(REGIONS_COUNT)
                        .append(" регионов, padNumber оптимизирован, с пулом генерации на ").append(GENERATE_THREADS_COUNT)
                        .append(" потоков, запись в 1 файл,").append(" в ").append(WRITE_THREADS_COUNT)
                        .append(" потоков, заняло времени - ").append(measuredTime).append(" мс");
                timeWriter.write(result.toString().getBytes());
                System.out.println(result.toString());
                timeWriter.flush();
                timeWriter.close();
            } else {
                System.out.println("Pool не завершил работу");
            }
            deleteCreatedFile(); // Удаляем созданные файлы
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void writeTheFile() throws Exception {
        ThreadPoolExecutor writePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(WRITE_THREADS_COUNT);
        PrintWriter printWriter = new PrintWriter(RESULT_FILE_OUTPUT);
        AtomicBoolean isWriteFinished = new AtomicBoolean(false);
        for (int i = 0; i < WRITE_THREADS_COUNT; i++) {
            writePool.submit(new Thread(() -> {
                while (regionsFinished.size() < REGIONS_COUNT - 1) { // Пока не все регионы записаны, пишем и ждем новые данные
                    while (stringQueue.peek() != null) { // Пишем пока очередь не пуста
                        printWriter.write(stringQueue.poll().toString());
                    }
                }
                if (stringQueue.peek() != null) { // Если последний регион получили, а очередь не пуста, допишем
                    System.out.println("Дописываем остатки");
                    while (stringQueue.peek() != null) { // Пишем пока очередь не пуста
                        printWriter.write(stringQueue.poll().toString());
                    }
                } // Очередь на запись пуста и все регионы обработаны
                isWriteFinished.set(true);
            }));
        }
        while (true) {
            if (isWriteFinished.get()) {
                writePool.shutdown();
                System.out.println("== Все данные записаны ==");
                printWriter.flush();
                printWriter.close();
                break;
            }
            Thread.sleep(10);
        }
    }

    private static void deleteCreatedFile() throws Exception {
        while (true) {
            System.out.println("Вы хотите удалить созданные файлы? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine().toUpperCase().trim();
            if (answer.equals("Y")) {
                Files.deleteIfExists(Path.of(RESULT_FILE_OUTPUT));
                Files.deleteIfExists(Path.of(TIMESTAMP_FILE));
                break;
            } else if (answer.equals("N")) {
                break;
            } else {
                System.out.println("Извините, команда не распознана");
            }
        }
    }
}