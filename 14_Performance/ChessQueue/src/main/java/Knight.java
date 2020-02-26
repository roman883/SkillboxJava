import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Knight extends Figure {

    // Coordinates
    int coordX;
    int coordY;
    Colors color;
    FigureNames figureName;
    int chessBoardSize;
    int countMoves;
    ArrayList<Knight> possibleNewPositions = new ArrayList<Knight>();
    private static ArrayList<Integer[]> madeMoves = new ArrayList<Integer[]>();
    private static Integer[] targetPosition;
    private Queue<Knight> queue = new LinkedBlockingQueue<Knight>();

    // Possible Moves [x, y]
    int[][] possibleMoves = {{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-2, 1}, {-2, -1}, {-1, -2}, {-1, 2}};

    public Knight(int startCol, int startRow, Colors color, FigureNames figureName,
                  int chessBoardSize, int endCol, int endRow) {
        this.coordX = startCol;
        this.coordY = startRow;
        this.color = color;
        this.figureName = figureName;
        this.chessBoardSize = chessBoardSize;
        countMoves = 0;
        Knight.targetPosition = new Integer[]{endCol, endRow};
    }

    public Knight(int coordX, int coordY, Colors color, FigureNames figureName, int chessBoardSize, int countMoves) { //  ArrayList<Integer[]> madeMoves
        this.coordX = coordX;
        this.coordY = coordY;
        this.color = color;
        this.figureName = figureName;
        this.chessBoardSize = chessBoardSize;
        this.countMoves = countMoves;
//        Knight.madeMoves = madeMoves;
    }

    public int calculateMovesToTarget(Knight initKnight) {
        queue.add(initKnight);
        while (queue.peek() != null) {
            Knight knight = queue.poll();
            int newCountMoves = knight.getCountMoves() + 1; // Устанавливаем номер хода
//            Integer[] coords = {knight.getCoordX(), knight.getCoordY()}; // Тек координаты
            for (int[] move : knight.getPossibleMoves()) { // Все возможные ходы из текущего положения
                Integer[] newCoords = {knight.getCoordX() + move[0], knight.getCoordY() + move[1]};
                if (hasValidMoves(newCoords)) { // Если не конец доски и ранее непосещенная клетка
//                System.out.println("Делаем ход " + coords[0] + " => " + newCoords[0] + ", " + coords[1] + " => " + newCoords[1]);
                    knight.getMadeMoves().add(newCoords);
                    Knight subPosition = new Knight(newCoords[0], newCoords[1], knight.getColor(),
                            knight.getFigureName(), knight.getChessBoardSize(), newCountMoves); // knight.getMadeMoves()
//                System.out.println("MadeMoves size - " + knight.getMadeMoves().size());
                    // Добавляем в очередь, которую позже
                    queue.add(subPosition);
                    if (Arrays.equals(targetPosition, newCoords)) { // Если наша финальная точка
//                        System.out.println("Достигли точки назначения за " + subPosition.getCountMoves() + " ходов");
                        return subPosition.getCountMoves();
                    }
//                else {
//                    System.out.println("Ход невозможен");
//                }
                }
            }
        }

        return -1;
    }

//    // Moves
//    public boolean move1() {
//        int newCoordX = coordX + possibleMoves[1][0];
//        int newCoordY = coordY + possibleMoves[1][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }
//
//    public boolean move2() {
//        int newCoordX = coordX + possibleMoves[2][0];
//        int newCoordY = coordY + possibleMoves[2][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }
//
//    public boolean move3() {
//        int newCoordX = coordX + possibleMoves[3][0];
//        int newCoordY = coordY + possibleMoves[3][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }
//
//    public boolean move4() {
//        int newCoordX = coordX + possibleMoves[4][0];
//        int newCoordY = coordY + possibleMoves[4][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }
//
//    public boolean move5() {
//        int newCoordX = coordX + possibleMoves[5][0];
//        int newCoordY = coordY + possibleMoves[5][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }
//
//    public boolean move6() {
//        int newCoordX = coordX + possibleMoves[6][0];
//        int newCoordY = coordY + possibleMoves[6][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }
//
//    public boolean move7() {
//        int newCoordX = coordX + possibleMoves[7][0];
//        int newCoordY = coordY + possibleMoves[7][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }
//
//    public boolean move8() {
//        int newCoordX = coordX + possibleMoves[0][0];
//        int newCoordY = coordY + possibleMoves[0][1];
//        if (isMoveValid(newCoordX, newCoordY)) {
//            coordX = newCoordX;
//            coordY = newCoordY;
//            return true;
//        } else {
//            System.out.println("Ход невозможен");
//            return false;
//        }
//    }

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

    public FigureNames getFigureName() {
        return figureName;
    }

    public void setFigureName(FigureNames figureName) {
        this.figureName = figureName;
    }

    public int getChessBoardSize() {
        return chessBoardSize;
    }

    public void setChessBoardSize(int chessBoardSize) {
        this.chessBoardSize = chessBoardSize;
    }

    boolean isMoveValid(Integer[] newCoords) {
        return newCoords[0] >= 0 && newCoords[0] < chessBoardSize && newCoords[1] >= 0 && newCoords[1] < chessBoardSize;
    }

    public int getCountMoves() {
        return countMoves;
    }

    public void setCountMoves(int countMoves) {
        this.countMoves = countMoves;
    }

    public ArrayList<Knight> getPossibleNewPositions() {
        return possibleNewPositions;
    }

    public void setPossibleNewPositions(ArrayList<Knight> possibleNewPositions) {
        this.possibleNewPositions = possibleNewPositions;
    }

    public int[][] getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(int[][] possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public void addSubPosition(Knight knight) {
//        possibleNewPositions.add(knight);
        madeMoves.add(new Integer[]{knight.getCoordX(), knight.getCoordY()});
    }

    boolean hasValidMoves(Integer[] newCoords) {
        boolean hasMadeMoves = false;
        for (Integer[] coords : madeMoves) { // Заменить проверку на mademoves.contains?
            if (Arrays.equals(coords, newCoords)) {
                hasMadeMoves = true;
//                System.out.println("Движение уже было");
            }
        }
        return !hasMadeMoves && isMoveValid(newCoords);
    }

    public ArrayList<Integer[]> getMadeMoves() {
        return madeMoves;
    }
}
