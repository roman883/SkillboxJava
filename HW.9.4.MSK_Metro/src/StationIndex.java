import java.util.HashMap;
import java.util.TreeSet;

public class StationIndex {

    private HashMap<String, Line> number2line;
    private TreeSet<Station> stations;

    public void addLine(Line line) {
        number2line.put(line.getNumber(), line);
    }

    public StationIndex() {
        number2line = new HashMap<String, Line>();
        stations = new TreeSet<>();
    }

    public Line getLine(String number)
    {
        return number2line.get(number);
    }

    public Station getStation(String name)
    {
        for(Station station : stations)
        {
            if(station.getName().equalsIgnoreCase(name)) {
                return station;
            }
        }
        return null;
    }

    public Station getStation(String name, String lineNumber)
    {
        Station query = new Station(name, getLine(lineNumber));
        Station station = stations.ceiling(query);
        return station.equals(query) ? station : null;
    }

    public void addStation(Station station)
    {
        stations.add(station);
    }

    public TreeSet<Station> getAllStations() {
               return stations;
    }

    public HashMap<String, Line> getAllLines() {
        return number2line;
    }
}
