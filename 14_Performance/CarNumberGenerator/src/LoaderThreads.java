import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoaderThreads {
    public static void main(String[] args) throws Exception {
        final int THREADS_COUNT = 15;
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS_COUNT);
        long start = System.currentTimeMillis();
        FileOutputStream timeWriter = new FileOutputStream("res/time.txt");

        char letters[] = {'У', 'К', 'Е', 'Н', 'Х', 'В', 'А', 'Р', 'О', 'С', 'М', 'Т'};
        for (int regionCode = 1; regionCode < 100; regionCode++) {
            int finalRegionCode = regionCode;
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        PrintWriter printWriter = new PrintWriter("res/numbers" + finalRegionCode + ".txt");
                        StringBuilder builder = new StringBuilder();
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
                        printWriter.write(builder.toString());
                        printWriter.flush();
                        printWriter.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        }
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
