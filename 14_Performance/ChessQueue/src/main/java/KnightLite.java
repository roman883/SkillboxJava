import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class KnightLite extends Figure {

    int coordX;
    int coordY;
    int chessBoardSize;
    int countMoves;
    private static HashSet<ArrayWrapper> madeMoves = new HashSet<>();
    private static Integer[] targetPosition;
    private Queue<KnightLite> queue = new LinkedBlockingQueue<KnightLite>();

    // Possible Moves [x, y]
    int[][] possibleMoves = {{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-2, 1}, {-2, -1}, {-1, -2}, {-1, 2}};

    public KnightLite(int startCol, int startRow, int chessBoardSize, int endCol, int endRow) {
        this.coordX = startCol;
        this.coordY = startRow;
        this.chessBoardSize = chessBoardSize;
        countMoves = 0;
        KnightLite.targetPosition = new Integer[]{endCol, endRow};
    }

    public KnightLite(int coordX, int coordY, int chessBoardSize, int countMoves) { //  ArrayList<Integer[]> madeMoves
        this.coordX = coordX;
        this.coordY = coordY;
        this.chessBoardSize = chessBoardSize;
        this.countMoves = countMoves;
//        Knight.madeMoves = madeMoves;
    }

    public int calculateMovesToTarget(KnightLite initKnight) {
        queue.add(initKnight);
        while (queue.peek() != null) {
            KnightLite knight = queue.poll();
            int newCountMoves = knight.getCountMoves() + 1; // Устанавливаем номер хода
            for (int[] move : knight.getPossibleMoves()) { // Все возможные ходы из текущего положения
                Integer[] newCoords = {knight.getCoordX() + move[0], knight.getCoordY() + move[1]};
                ArrayWrapper newCoordsWrapper = new ArrayWrapper(newCoords);
                if (hasValidMoves(newCoordsWrapper)) { // Если не конец доски и ранее непосещенная клетка
                    System.out.println("Делаем ход " + knight.coordX + " => " + newCoords[0] + ", " + knight.coordY + " => " + newCoords[1]);
                    knight.getMadeMoves().add(newCoordsWrapper);
                    KnightLite subPosition = new KnightLite(newCoords[0], newCoords[1], knight.getChessBoardSize(), newCountMoves);
                    queue.add(subPosition);
                    if (Arrays.equals(targetPosition, newCoords)) { // Если наша финальная точка
                        return subPosition.getCountMoves();
                    }
                }
            }
        }

        return -1;
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }

    public int getChessBoardSize() {
        return chessBoardSize;
    }

    boolean isMoveValid(Integer[] newCoords) {
        return newCoords[0] >= 0 && newCoords[0] < chessBoardSize && newCoords[1] >= 0 && newCoords[1] < chessBoardSize;
    }

    public int getCountMoves() {
        return countMoves;
    }

    public int[][] getPossibleMoves() {
        return possibleMoves;
    }

    boolean hasValidMoves(ArrayWrapper newCoords) {
        boolean hasMadeMoves = false;
        if (madeMoves.contains(newCoords)) {
            hasMadeMoves = true;
//                System.out.println("Движение уже было");
        }
        return !hasMadeMoves && isMoveValid(newCoords.array);
    }

    public HashSet<ArrayWrapper> getMadeMoves() {
        return madeMoves;
    }
}
