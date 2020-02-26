public class Main {
    // Вариант 1
    // При размере доски 8, начальной точке 2-1 достигаем точки 7-5 за 3 хода
    // При увеличении размера доски до 1000 столкнулся со StackOverFlow, принял решение использовать очередь

    // Вариант 2, с промежуточной очередью для работы, сначала наполняем её новыми значения для расчетов,
    // и далее в цикле каждый раз обрабатываем и наполняем её снова, так пока она не опустеет
    // При размере доски 1000, и началом в 1:1 - также 3 хода и не получаю StackOverFlow
    // Xmx1024M - При начале в 1000:1000 и целью 7-5 Достигли точки назначения за 664 ходов
    // Памяти использовано: 77 МБ
    // Xmx128M - также работает, время работы очень долгое (проверка сделанных движений по перебору ArrayList всех массивов).

    // Вариант 3.
    // Создана лайт версия класса KnightLite и для реализации contains hashset для массивов в сете класс обертка ArrayWrapper
    // Время работы 7-15 секунд. Результат тот же. Мин.память - 256 МБ ОК. 128 Оказалось мало.

    private static final int CHESSBOARD_SIZE = 1000; // width == height
    private static int startCol = 1000;
    private static int startRow = 1000;
    private static int endCol = 7;
    private static int endRow = 5;

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        ChessBoard chessBoard = new ChessBoard(CHESSBOARD_SIZE);

        // Базовая "тяжелая" и очень долгая версия, с переборкой всех элементов массивов
//        Knight knight = new Knight(startCol, startRow, Colors.BLACK, FigureNames.KNIGHT, CHESSBOARD_SIZE, endCol, endRow);

        // Lite-версия, с переопределенными hash И equals для класса-оболочки
        KnightLite knight = new KnightLite(startCol, startRow, CHESSBOARD_SIZE, endCol, endRow);
        chessBoard.addFigure(knight);
        int countMoves = knight.calculateMovesToTarget(knight);
        if (countMoves == -1) {
            System.out.println("Невозможно достигнуть точки назначения " + endCol + ":" + endRow + " из стартового положения " + startCol + ":" + startRow);
        } else {
            System.out.println("Достигли точки назначения " + endCol + ":" + endRow + " из стартового положения " + startCol + ":" +startRow + " за " + countMoves + " ходов!");
        }
        long newMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memory;
        System.out.println("Памяти использовано: " + newMemory / 1048576 + " МБ");
        System.out.println("Времени затрачено " + (System.currentTimeMillis() - start) / 1000 + " c");
    }
}