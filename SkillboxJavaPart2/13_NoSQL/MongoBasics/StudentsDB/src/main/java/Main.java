import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Main {
    private static final String COLLECTION_NAME = "Students";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 27017;
    private static final String NAME = "Name";
    private static final String AGE = "Age";
    private static final String COURSES = "Courses";
    private static final String DATABASE_NAME = "local";

    private static String dataFile = "data/mongo.csv";

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient(HOST, PORT);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME); // Получаем коллекцию
        collection.drop(); // Удалим из нее все документы, на случай, если она не пустая

        List<String> lines = parseTheFile(dataFile); // парсим csv
        fillTheDB(lines, collection); // Коллекция наполняется
        getSomeResults(collection); // результат
    }

    private static List<String> parseTheFile(String dataFilePath) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(dataFilePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }

    private static void fillTheDB(List<String> lines, MongoCollection<Document> collection) {
        int count = 0;
        for (String line : lines) {
            String[] splitString = line.split(",", 3);
            String studentName = splitString[0];
            int studentAge = Integer.parseInt(splitString[1]);
            String[] courses = splitString[2].replace("\"", "").split(",");
            String tempCoursesString = "";
            for (int i = 0; i < courses.length; i++) {
                if (!tempCoursesString.equals("")) {
                    tempCoursesString += ", " + courses[i];
                } else {
                    tempCoursesString += courses[i];
                }
            }
            if (count <= 50) { // Если первые 50 объектов, создаем таким способом
                Document document = new Document()
                        .append(NAME, studentName)
                        .append(AGE, studentAge)
                        .append(COURSES, tempCoursesString);
                collection.insertOne(document);
            } else { // остальные добавляем используя JSON синтаксис
                String jsonString = "{" + NAME + ": \"" + studentName + "\", "
                        + AGE + ": " + studentAge + ", "
                        + COURSES + ": \"" + tempCoursesString + "\"}";
                Document document = Document.parse(jsonString);
                collection.insertOne(document);
            }
            count++;
        }
    }

    private static void getSomeResults(MongoCollection<Document> collection) {
        long studentsDBcount = collection.countDocuments();
        System.out.println("Общее количество студентов в базе: " + studentsDBcount);

        ArrayList<String> studentsOver40 = new ArrayList<>();
        collection.find(gt(AGE, 40))
                .forEach((Consumer<Document>) document -> studentsOver40.add(document.toString()));
        System.out.println("Общее количество студентов старше 40 лет: " + studentsOver40.size());

        String minAgeStudentName = (String) collection.find()
                .sort(BsonDocument.parse("{" + AGE + ": 1}")).limit(1).first().get(NAME);
        System.out.println("Имя одного самого молодого студента: " + minAgeStudentName);

        String maxAgeStudentCourses = (String) collection.find()
                .sort(BsonDocument.parse("{" + AGE + ": -1}")).limit(1).first().get(COURSES);
        System.out.println("Список курсов одного самого старого студента: " + maxAgeStudentCourses);

        // Списки курсов нескольких самых старых студентов (одного возраста)
        ArrayList<String> coursesList = new ArrayList<>();
        int maxAge = (int) collection.find().sort(BsonDocument.parse("{" + AGE + ": -1}")).limit(1).first().get(AGE);
        BsonDocument query = BsonDocument.parse("{" + AGE + ": {$eq: " + maxAge +"}}");
        collection.find(query).forEach((Consumer<Document>) doc -> {
                    String tempString = doc.get(NAME) + ", Age - " + doc.get(AGE) + ", Courses: " + doc.get(COURSES);
                    coursesList.add(tempString);
                });
        System.out.println("=============================\nСписки курсов нескольких самых старых студентов");
        coursesList.forEach(System.out::println);
    }
}