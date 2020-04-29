import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFileCreator {

    public static void createJsonDocument(StationIndex stationIndex) {
        JSONObject jsonObject = new JSONObject();
        JSONObject stationsObject = new JSONObject();
        JSONArray linesJsonArray = new JSONArray();
        String numberLineString = "number";
        String nameLineString = "name";

        jsonObject.put("Stations", stationsObject);
        jsonObject.put("Lines", linesJsonArray);

        for (String key : stationIndex.getAllLines().keySet()) {
            JSONArray tempStationsStringArray = new JSONArray();
            stationIndex.getAllLines().get(key).getStations().forEach(station -> tempStationsStringArray.add(station.getName()));
            stationsObject.put(key, tempStationsStringArray);
        }
        for (String key : stationIndex.getAllLines().keySet()) {
            JSONObject tempLineJsonObject = new JSONObject();
            tempLineJsonObject.put(numberLineString, stationIndex.getLine(key).getNumber());
            tempLineJsonObject.put(nameLineString, stationIndex.getLine(key).getName());
            linesJsonArray.add(tempLineJsonObject);
        }

        try {
            String fileOutputPath = "tmp/output.json";
            Path directoryPath = Path.of(fileOutputPath.replaceAll("\\/.+", ""));
            if (!Files.exists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
            FileWriter file = new FileWriter(fileOutputPath);
            file.write(jsonObject.toJSONString());
            file.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
