import java.util.ArrayList;

public class ChessBoard {

    // Доска квадратная, так что имеем только одну переменную
    int size; // = height = width
    ArrayList<Figure> figures;

    public ChessBoard(int size, ArrayList<Figure> figures) {
        this.size = size;
        this.figures = figures;
    }

    public ChessBoard(int size) {
        this.size = size;
        figures = new ArrayList<Figure>();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<Figure> getFigures() {
        return figures;
    }

    public void setFigures(ArrayList<Figure> figures) {
        this.figures = figures;
    }

    public void addFigure(Figure figure) {
        figures.add(figure);
    }
}
