import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChessKnight {

    static final int[] POSSIBLE_MOVES_X = {2, 2, 1, 1, -2, -2, -1, -1}; // возможные ходы X
    static final int[] POSSIBLE_MOVES_Y = {1, -1, 2, -2, 1, -1, -2, 2}; // возможные ходы Y

    public static int countMoves(
            int width, int height,
            int startCol, int startRow,
            int endCol, int endRow) {
        //TODO: code here
        Queue<Position> queue = new LinkedBlockingQueue<>(); // очередь необработанных исходных положений для поиска ходов
        HashSet<Position> madeMoves = new HashSet<>();
        queue.add(new Position(startCol, startRow)); // добавляем старт.позицию
        Position endPosition = new Position(endCol, endRow);
        int sumEnd = endCol + endRow;
        int countMove = 0; // Счетчик ходов
        while (queue.peek() != null) {
            int queueSize = queue.size();
            for (int i = 0; i < queueSize; i++) { // Выполняем только для текущих элементов в очереди (номер хода)
                Position currentPosition = queue.poll();
                if (endPosition.equals(currentPosition)) { // Если это наша финальная точка, то выводим результат
                    return countMove;
                }
                for (int j = 0; j < POSSIBLE_MOVES_X.length; j++) {
                    int newX = currentPosition.array[0] + POSSIBLE_MOVES_X[j];
                    int newY = currentPosition.array[1] + POSSIBLE_MOVES_Y[j];
                    // проверяем на возможность хода и в случае успеха добавляем в очередь
                    if (newX > 0 && newX <= width && newY > 0 && newY <= height) {
                        int sumCurrent = currentPosition.array[0] + currentPosition.array[1];
                        int sumNew = newX + newY;
                        Position newPosition = new Position(newX, newY);
                        if (!madeMoves.contains(newPosition)) {
                            // Если расстояние до конечной точки меньше либо равно 4, то добавляем в очередь все возможные ходы (включая назад)
                            if (Math.abs(sumEnd - sumCurrent) <= 4) {
                                queue.add(newPosition);
                                madeMoves.add(newPosition);
                            } else { // Иначе добавляем только те, что сокращают расстояние
                                if (Math.abs(sumEnd - sumCurrent) > Math.abs(sumEnd - sumNew)) {
                                    queue.add(newPosition);
                                    madeMoves.add(newPosition);
                                }
                            }
                        }
                    }
                }
            }
            countMove++;
        }
        return -1;
    }

    static class Position { // доп.класс для хранения и сравнения координат
        Integer[] array;
        Position(int x, int y) {
            array = new Integer[]{x, y};
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return Arrays.equals(array, position.array);
        }
        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }
}