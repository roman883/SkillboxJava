import java.util.Queue;

public class Generator implements Runnable {

    private int regionCode;
    private Queue<StringBuilder> stringBuilderQueue;
    private Queue<Boolean> regionsFinished;

    private static final int PIECE_SIZE = 2_048;
    private static final char[] LETTERS = {'У', 'К', 'Е', 'Н', 'Х', 'В', 'А', 'Р', 'О', 'С', 'М', 'Т'};
    private static final int MAX_STRING_QUEUE_SIZE = 50;

    public Generator(int regionCode, Queue<StringBuilder> stringBuilderQueue, Queue<Boolean> regionsFinished) {
        this.regionCode = regionCode;
        this.stringBuilderQueue = stringBuilderQueue;
        this.regionsFinished = regionsFinished;
    }

    public void run() {
        try {
            StringBuilder builder = new StringBuilder();
            for (int number = 1; number < 1000; number++) {
                for (char firstLetter : LETTERS) {
                    for (char secondLetter : LETTERS) {
                        for (char thirdLetter : LETTERS) {
                                while (stringBuilderQueue.size() > MAX_STRING_QUEUE_SIZE) {
                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            builder.append(firstLetter).append(padNumber(number, 3))
                                    .append(secondLetter).append(thirdLetter)
                                    .append(padNumber(regionCode, 2)).append("\n");
                            if (builder.length() >= PIECE_SIZE) { // Если размер в буфере стал больше или равен PIECE_SIZE, то отправляем в очередь на запись
                                    stringBuilderQueue.add(builder);
                                builder = new StringBuilder();
                            }
                            if (number == 999 && thirdLetter == LETTERS[LETTERS.length - 1] // Если последний номер
                                    && secondLetter == LETTERS[LETTERS.length - 1]
                                    && firstLetter == LETTERS[LETTERS.length - 1]) {
                                    stringBuilderQueue.add(builder);
                                    regionsFinished.add(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
