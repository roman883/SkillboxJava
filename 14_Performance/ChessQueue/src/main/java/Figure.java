public abstract class Figure {

    int coordX;
    int coordY;
    Colors color;

    public Figure(int coordX, int coordY, Colors color) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.color = color;
    }

    public Figure() {
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
}
