import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonFileReader {

    private static StationIndex stationIndex;

    private static Path pathToFile;

    public JsonFileReader(Path pathToFile) {
        JsonFileReader.pathToFile = pathToFile;
    }

    private static String readJsonFile(Path pathToJsonFile) {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(pathToJsonFile);
            lines.forEach(line -> builder.append(line));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private static void parseStations(JSONObject stationsObject) {

        stationsObject.keySet().forEach(lineNumberObject ->
        {
            String lineNumber = (String) lineNumberObject;
            Line line = stationIndex.getLine(lineNumber);
            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
            stationsArray.forEach(stationObject -> {
                Station station = new Station((String) stationObject, line);
                stationIndex.addStation(station);
                line.addStation(station);
            });
        });
    }

    public static void createStationIndex() {
        stationIndex = new StationIndex();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject mainJsonObject = (JSONObject) jsonParser.parse(readJsonFile(pathToFile));

            JSONArray linesArray = (JSONArray) mainJsonObject.get("Lines");
            parseLines(linesArray);

            JSONObject stationsObject = (JSONObject) mainJsonObject.get("Stations");
            parseStations(stationsObject);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void parseLines(JSONArray linesArray) {
        linesArray.forEach(lineObject -> {
            JSONObject lineJsonObject = (JSONObject) lineObject;
            Line line = new Line(
                    (String) lineJsonObject.get("number"),
                    (String) lineJsonObject.get("name"));
            stationIndex.addLine(line);
        });
    }

    public static StationIndex getStationIndex() {
        return stationIndex;
    }

    public static Path getPathToFile() {
        return pathToFile;
    }

    public static void setPathToFile(Path pathToFile) {
        JsonFileReader.pathToFile = pathToFile;
    }
}
