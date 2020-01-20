import java.util.ArrayList;
import java.util.List;

public class Line implements Comparable<Line> {

    private String number;
    private String name;
    private String lineColor;
    private List<Station> stations;

    public Line(String number, String name) {
        this.number = number;
        this.name = name;
        stations = new ArrayList<>();
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    @Override
    public int compareTo(Line line) {
        return CharSequence.compare(number, line.getNumber());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Line) obj) == 0;
    }

    @Override
    public String toString() {
        return "Линия " + number +" " +  name;
    }
}

