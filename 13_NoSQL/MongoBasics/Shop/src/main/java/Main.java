import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.UnwindOptions;
import org.bson.BsonDocument;
import org.bson.Document;

import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.addToSet;

public class Main {

    private static final String SHOP_COLLECTION_NAME = "Shops";
    private static final String ITEM_COLLECTION_NAME = "Items";
    private static final String DATABASE_NAME = "local";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 27017;
    private static final String SHOP_NAME = "Name";
    private static final String ITEM_LIST = "Items";
    private static final String ITEM_NAME = "Name";
    private static final String ITEM_PRICE = "Price";
    private static final String INSERT_SHOP_COMMAND = "ДОБАВИТЬ_МАГАЗИН";
    private static final String INSERT_ITEM_COMMAND = "ДОБАВИТЬ_ТОВАР";
    private static final String INSERT_ITEM_TO_SHOP_COMMAND = "ВЫСТАВИТЬ_ТОВАР";
    private static final String STATISTICS_COMMAND = "СТАТИСТИКА_ТОВАРОВ";
    private static final String CLEAN_COMMAND = "ОЧИСТИТЬ";
    private static final String EXIT_COMMAND = "ЗАВЕРШИТЬ";

    private static MongoCollection<Document> shopCollection;
    private static MongoCollection<Document> itemCollection;
    private static MongoClient mongoClient;

    public static void main(String[] args) {
        initDB();
        for (; ; ) {
            String input = getInputLine();
            String[] command = getCommand(input);
            if (command != null) {
                executeCommand(command);
            } else {
                closeDB();
                break;
            }
        }
    }

    private static void executeCommand(String[] command) {
        Document document = new Document();
        switch (command[0]) {
            case (INSERT_SHOP_COMMAND):
                Document foundShop = getShop(command[1]); // Проверка наличия такого магазина в базе
                if (foundShop != null) {
                    System.out.println("Данный магазин уже есть в базе.");
                    break;
                }
                document.append(SHOP_NAME, command[1]).append(ITEM_LIST, Collections.emptyList());
                shopCollection.insertOne(document);
                System.out.println("Добавлен магазин " + document);
                break;
            case (INSERT_ITEM_COMMAND):
                Document foundItem = getItem(command[1]); // Проверка наличия такого товара в базе
                if (foundItem != null) {
                    System.out.println("Данный товар уже есть в базе.");
                    break;
                }
                document.append(ITEM_NAME, command[1]).append(ITEM_PRICE, Integer.parseInt(command[2]));
                itemCollection.insertOne(document);
                System.out.println("Добавлен товар " + document);
                break;
            case (INSERT_ITEM_TO_SHOP_COMMAND):
                foundItem = getItem(command[1]);
                if (foundItem == null) { // Проверка наличия товара в базе
                    System.out.println("Такой товар не найден.");
                    break;
                }
                foundShop = getShop(command[2]);
                if (foundShop == null) { // Проверка наличия магазина в базе
                    System.out.println("Такой магазин не найден.");
                    break;
                }
                Document doc = shopCollection.find(and(eq(SHOP_NAME, command[2]), all(ITEM_LIST, command[1]))).first();
                if (doc != null) { // Проверяем есть ли уже такой товар в списке заданного магазина
                    System.out.println("Товар уже добавлен в магазин");
                    break;
                }
                // Добавляем дополнительный товар
                shopCollection.updateOne(eq(SHOP_NAME, command[2]), addToSet(ITEM_LIST, command[1]));
                System.out.println("Выставлен товар \"" + command[1] + "\" в магазин \"" + command[2] + "\"");
                break;
            case (STATISTICS_COMMAND):
                System.out.println("-------------------------------");
                AggregateIterable<Document> aggregatedData = aggregateData();
                for (Document d : aggregatedData) {
                    System.out.println("Данные для магазина " + d.get("_id") + ":");
                    System.out.println("\t- общее количество наименований товара - " + d.get("countItems"));
                    System.out.println("\t- наибольшая цена товара - " + d.get("maxPrice"));
                    System.out.println("\t- наименьшая цена товара - " + d.get("minPrice"));
                    System.out.println("\t- средняя цена товара - " + d.get("avgPrice"));
                }
                System.out.println("-------------------------------");
                break;
            case (CLEAN_COMMAND):
                resetDB();
                break;
            default:
                System.out.println("Неизвестная комманда, проверьте код");
                break;
        }
    }

    private static AggregateIterable<Document> aggregateData() {
        return shopCollection.aggregate(Arrays.asList(
                Aggregates.lookup(                                      // Соединяем с данными из коллекции по товарам по названию товара
                        itemCollection.getNamespace().getCollectionName(), ITEM_LIST, ITEM_NAME, "itemList"),
                // разбиваем полученные массивы с документами с ценами товаров
                Aggregates.unwind("$" + "itemList", new UnwindOptions().preserveNullAndEmptyArrays(true)),
                Aggregates.group("$" + SHOP_NAME,                   // группируем статистику по магазинам
                        Accumulators.sum("countItems", 1),
                        Accumulators.avg("avgPrice", "$itemList." + ITEM_PRICE),
                        Accumulators.max("maxPrice", "$itemList." + ITEM_PRICE),
                        Accumulators.min("minPrice", "$itemList." + ITEM_PRICE))));
    }

    private static String getInputLine() {
        System.out.println("Введите команду:");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static String[] getCommand(String input) {
        String regexInsertShop = INSERT_SHOP_COMMAND + "\\s+\\S+";
        String regexInsertItem = INSERT_ITEM_COMMAND + "\\s+\\S+\\s+\\d+";
        String regexInsertItemToShop = INSERT_ITEM_TO_SHOP_COMMAND + "\\s+\\S+\\s+\\S+";
        String tempInput = input.trim().toUpperCase();
        String[] command = null;
        String[] tempString = input.trim().split("\\s+");
        if (tempInput.matches(regexInsertShop)) {
            command = new String[2];
            command[0] = tempString[0].toUpperCase();
            command[1] = tempString[1];
        } else if (tempInput.matches(regexInsertItem) || tempInput.matches(regexInsertItemToShop)) {
            command = new String[3];
            command[0] = tempString[0].toUpperCase();
            command[1] = tempString[1];
            command[2] = tempString[2];
        } else if (tempInput.matches(STATISTICS_COMMAND) || tempInput.matches(CLEAN_COMMAND)) {
            command = new String[1];
            command[0] = tempInput.toUpperCase();
        } else if (tempInput.matches(EXIT_COMMAND)) {
            command = null;
        } else {
            System.out.println("Команда не распознана");
        }
        return command;
    }

    private static void initDB() {
        mongoClient = new MongoClient(HOST, PORT);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        shopCollection = database.getCollection(SHOP_COLLECTION_NAME); // Получаем коллекцию
        itemCollection = database.getCollection(ITEM_COLLECTION_NAME); // Получаем коллекцию
        System.out.println("== Доступные команды: ==");
        System.out.println("=>\t\"" + INSERT_SHOP_COMMAND + " НазваниеМагазина\"");
        System.out.println("=>\t\"" + INSERT_ITEM_COMMAND + " НазваниеТовара ЦенаТовара\"");
        System.out.println("=>\t\"" + INSERT_ITEM_TO_SHOP_COMMAND + " НазваниеТовара НазваниеМагазина\"");
        System.out.println("=>\t\"" + STATISTICS_COMMAND + "\"");
        System.out.println("=>\t\"" + CLEAN_COMMAND + "\"");
        System.out.println("=>\t\"" + EXIT_COMMAND + "\"");
    }

    private static void resetDB() {
        shopCollection.drop();
        itemCollection.drop();
        System.out.println("База данных очищена");
        initDB();
    }

    private static void closeDB() {
        mongoClient.close();
        System.out.println("Завершена работа с базой данных");
    }

    private static Document getShop(String shopName) {
        BsonDocument query = BsonDocument.parse("{" + SHOP_NAME + ": {$eq: \"" + shopName + "\"}}");
        return shopCollection.find(query).first();
    }

    private static Document getItem(String itemName) {
        BsonDocument query = BsonDocument.parse("{" + ITEM_NAME + ": {$eq: \"" + itemName + "\"}}");
        return itemCollection.find(query).first();
    }
}