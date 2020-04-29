import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.sql.SQLException;
import java.util.Scanner;

public class Loader {

    public static void main(String[] args) throws Exception {
        String fileName = "E:/data-1572M.xml";

        long time = System.currentTimeMillis(); // Общее время работы
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); // Current memory
        // SAXparser - parse and upload
        long timeParseAndUpload = System.currentTimeMillis();
        parseFile(fileName);
        System.out.println("=> Файл спарсен и загружен в БД за " + (System.currentTimeMillis() - timeParseAndUpload) + " мс");

        System.out.println("=> Проголосовали более одного раза:");
        DBConnection.printVoterCounts();

        // Поиск по БД
        String personName = "Ванчиков Теймураз";
        searchPerson(personName);

        System.out.println("=> Поиск станции по номеру в БД:");
        Integer stationNumber = 18;
        System.out.println(DBConnection.serchStation(stationNumber));
        System.out.println("------------------");

        showAllStations();

        memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryUsage;
        System.out.println("===============\n=> Использовано памяти - " + memoryUsage / (1_048_576) + " MБ");
        System.out.println("=> Общее время работы " + (System.currentTimeMillis() - time) + " мс");
    }

    private static void searchPerson(String name) throws SQLException {
        long timeCustomSelectWorks = System.currentTimeMillis();
        int searchResult = DBConnection.CustomSelect(name);
        System.out.println("Поиск человека по БД отработал за " + (System.currentTimeMillis() - timeCustomSelectWorks) + " мс");
        if (searchResult == -1) {
            System.out.println(name + " - Отсутствует в БД");
        } else {
            System.out.println("Для " + name + ", id: " + searchResult);
        }
        System.out.println("-----------------------");
    }

    private static void parseFile(String fileName) throws Exception {
        // Парсим файл с данными и загружаем в БД. Используем SAXparser, multiInsert, StringBuilder
        // При загрузке задаем индекс - имя и составной индекс - имя+дата_рождения
        DBConnection.createPrepStatements();
        SAXParserFactory factoryX = SAXParserFactory.newInstance();
        SAXParser SAXparserX = factoryX.newSAXParser();
        XmlHandler handlerX = new XmlHandler();
        SAXparserX.parse(new File(fileName), handlerX);
        DBConnection.uploadLastPart();
    }

    private static void showAllStations() throws SQLException {
        System.out.println("Вы хотите вывести время работы всех станций? (Y/N)");
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine();
            if (answer.trim().toUpperCase().equals("Y")) {
                DBConnection.printStations();
                break;
            }
            else if (answer.trim().toUpperCase().equals("N")) {
                break;
            }
            else {
                System.out.println("Ответ не распознан");
            }
        }
    }
}