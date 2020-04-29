import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoaderThreadsOptim {

    public static void main(String[] args) throws Exception {

        final int THREADS_COUNT = 4;

        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_COUNT);
        long start = System.currentTimeMillis();
        FileOutputStream timeWriter = new FileOutputStream("res/time.txt");

        char letters[] = {'У', 'К', 'Е', 'Н', 'Х', 'В', 'А', 'Р', 'О', 'С', 'М', 'Т'};

        ArrayList<Future<StringBuilder>> list = new ArrayList<>();

        for (int regionCode = 1; regionCode < 100; regionCode++) {
            int finalRegionCode = regionCode;
            Future<StringBuilder> futureTask = threadPool.submit(new Callable<StringBuilder>() {
                @Override
                public StringBuilder call() {
                    StringBuilder builder = new StringBuilder();
                    try {
                        for (int number = 1; number < 1000; number++) {
                            for (char firstLetter : letters) {
                                for (char secondLetter : letters) {
                                    for (char thirdLetter : letters) {
                                        builder.append(firstLetter).append(padNumber(number, 3))
                                                .append(secondLetter).append(thirdLetter)
                                                .append(padNumber(finalRegionCode, 2)).append("\n");
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("Поймано исключение");
                        ex.printStackTrace();
                    }
                    return builder;
                }
            });
            list.add(futureTask);
        }
        // Наполнили ArrayList Future<StringBuilder>-ами и теперь все разом записываем
        PrintWriter printWriter = new PrintWriter("res/numbers.threads.txt");
        for (Future<StringBuilder> f : list) {
            for (;;) {
                if (f.isDone()) { // Записываем только завершенную задачу
                    try {
                        printWriter.write(f.get().toString());
                        break;
                    } catch (Exception ex) {
                        System.out.println("Поймано исключение при записи");
                        ex.printStackTrace();
                    }
                } else {
                    Thread.sleep(50); // Иначе ждем 50 мс и снова проверяем
                }
            }
        }
        printWriter.flush(); // Дописываем остатки если есть
        printWriter.close();
        threadPool.shutdown();
        for (; ; ) {
            if (threadPool.isTerminated()) {
                long measuredTime = (System.currentTimeMillis() - start);
                System.out.println(measuredTime + " ms");
                timeWriter.write(("Измеренное время с PrintWriter 100 регионов, padNumber оптимизирован, с пулом на " + THREADS_COUNT + " потоков, запис в 8 файлов мс - " + measuredTime).getBytes());
                break;
            }
        }
    }

    private static String padNumber(int number, int numberLength) {
        String numberStr = "";
        switch (numberLength) {
            case (2):
                if (number < 10) {
                    numberStr = "0" + number;
                } else {
                    numberStr = Integer.toString(number);
                }
                break;
            case (3):
                if (number < 10) {
                    numberStr = "00" + number;
                } else if (number < 100) {
                    numberStr = "0" + number;
                } else {
                    numberStr = Integer.toString(number);
                }
                break;
        }
        return numberStr;
    }
}
