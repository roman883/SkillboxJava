import java.nio.file.Path;

public class Main {

    public static void main (String[] args) {

        String urlString = "https://ru.wikipedia.org/wiki/Список станций Московского метрополитена";
        System.out.println("====== Парсим страницу " + urlString + " на предмет станций и линий ======");
        HtmlParser htmlParser = new HtmlParser(urlString);
        StationIndex stationIndex = htmlParser.parseTheUrl();
        System.out.println("====== Выводим результат парсинга и создания объектов с данными ======");
        stationIndex.getAllStations().forEach(System.out::println);
        stationIndex.getAllLines().keySet().forEach(i -> System.out.println(i + " " + stationIndex.getLine(i).toString()));
        System.out.println("====== Создаем JSON файл ======");
        JsonFileCreator.createJsonDocument(stationIndex);

        String pathToFile = "tmp/output.json";
        System.out.println("====== Снова читаем созданный файл " + pathToFile + " ======");
        JsonFileReader.setPathToFile(Path.of(pathToFile));
        JsonFileReader.createStationIndex();
        StationIndex stationIndexRead = JsonFileReader.getStationIndex();
        System.out.println("====== Выводим результаты по станциям и линиям ======");
        stationIndexRead.getAllStations().forEach(System.out::println);
        stationIndexRead.getAllLines().keySet().forEach(i -> System.out.println(i + " " + stationIndexRead.getLine(i).toString()));
    }
}