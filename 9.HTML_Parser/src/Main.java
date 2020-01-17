import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.InputStream;
import java.lang.String;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String url = "https://lenta.ru/";
        ArrayList<String> imgURLs = parseTheWebSite(url);
        imgURLs.forEach(System.out::println);
        downloadImages(imgURLs);
    }

    private static ArrayList<String> parseTheWebSite(String url) {
        ArrayList<String> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements pics = doc.select("section[class$=b-layout js-layout b-layout_main]").select("img");
            System.out.println("Найдены следующие изображения в основном теле документа: ");
            for (Element e : pics) {
                printTheString("%s \t %s", e.tagName(), e.attr("abs:src"));
                result.add(e.attr("abs:src"));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static void printTheString(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static void downloadImages(ArrayList<String> imgURLs) {
        try {
            String downloadDirectory = "data";
            if (!Files.exists(Path.of(downloadDirectory))) {
                Files.createDirectory(Path.of(downloadDirectory));
            }
            String regexFileName = ".+/";
            for (String u : imgURLs) {
                URLConnection connection = new URL(u).openConnection();
                InputStream inputStream = connection.getInputStream();
                String pathToFile = downloadDirectory + "/" + u.replaceAll(regexFileName, "");
                if (!Files.exists(Path.of(pathToFile))) {
                    Files.copy(inputStream, new File(pathToFile).toPath());
                } else {
                    System.out.println("Не удалось скачать файл " + u + ", т.к. он уже существует");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}