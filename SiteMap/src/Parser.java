import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Parser extends RecursiveTask<Page> {

    private Page page;
    private static String initUrl;
    private static CopyOnWriteArraySet<String> allLinksList;

    public Parser(String initUrl) {
        this.page = new Page(initUrl, 0);
        Parser.initUrl = initUrl;
        allLinksList = new CopyOnWriteArraySet<>();
        allLinksList.add(initUrl);
    }

    public Parser(Page page) {
        this.page = page;
        allLinksList.add(page.getUrl());
    }

    @Override
    protected Page compute() {
        String currentUrl = page.getUrl();
        System.out.println("Парсим страницу по адресу " + currentUrl);
        try {
            Thread.sleep((int) (Math.random() * 300) + 200); // Случайная задержка от 200 до 500 мс
            Document doc = Jsoup.connect(currentUrl).get();
            if (doc != null) {
                Elements elements = doc.select("a[href]");
                ArrayList<Parser> taskList = new ArrayList<>();
                for (Element e : elements) {
                    String link = e.attr("abs:href");
                    if (isLinkFitsUs(link)) {
                        Page newPage = new Page(link, (page.getPageLevel() + 1));
                        page.addOuterLink(newPage);
                        Parser task = new Parser(newPage);
                        task.fork();
                        taskList.add(task);
                    }
                }
                taskList.forEach(ForkJoinTask::join);
            }
        } catch (Exception ex) {
            System.out.println("Поймано исключение " + ex);
            ex.printStackTrace();
        }
        return page;
    }

    private boolean isLinkFitsUs(String link) {
        return link.startsWith(initUrl) && !link.contains("index") && !link.endsWith(".pdf") && !link.contains("#")
                && !allLinksList.contains(link) && !link.isEmpty() && !link.endsWith(".jpg") && !link.endsWith(".png")
                && !link.endsWith(".jpeg") && !link.endsWith(".doc") && !link.endsWith(".ico");
    }
}
