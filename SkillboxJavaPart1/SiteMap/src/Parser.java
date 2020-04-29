import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Parser extends RecursiveTask<Page> {

    private Page page;
    private String rootUrl;
    private volatile Set<String> visitedLinks;
    private volatile Queue<Page> queuePages;
    private static final String[] USER_AGENT_LIST = {
            "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36" +
                    " (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36 OPR/53.0.2907.68,gzip(gfe)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/66.0.3359.170 Safari/537.36 OPR/53.0.2907.99",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/66.0.3359.170 Safari/537.36 OPR/53.0.2907.58",
            "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko)" +
                    " Chrome/66.0.3359.139 Safari/537.36 OPR/53.0.2907.37 (Edition avira)"};
    private static final String[] REFERER_LIST = {
            "http://www.google.ru", "http://www.google.com", "http://www.yandex.ru", "http://www.bing.com"};

    public Parser(String rootUrl) {
        this.page = new Page(rootUrl, 0);
        this.rootUrl = rootUrl;
        this.visitedLinks = Collections.synchronizedSet(new HashSet<>());
        this.queuePages = new ConcurrentLinkedQueue<>();
        this.visitedLinks.add(rootUrl);
    }

    public Parser(Page page, String rootUrl, Set<String> visitedLinks, Queue<Page> queuePages) {
        this.page = page;
        this.rootUrl = rootUrl;
        this.visitedLinks = visitedLinks;
        this.queuePages = queuePages;
    }

    @Override
    protected Page compute() {
        String currentUrl = page.getUrl();
        HashSet<String> links = getLinks(currentUrl);
        ArrayList<Parser> taskList = new ArrayList<>();
        for (String link : links) {
            if (!visitedLinks.contains(link)) {
                Page newPage = new Page(link, (page.getPageLevel() + 1));
                queuePages.add(newPage);
                visitedLinks.add(link);
            }
        }
        while (queuePages.peek() != null) {
            Page tempPage = queuePages.poll();
            Parser task = new Parser(tempPage, rootUrl, visitedLinks, queuePages); // создается задача
            page.addSubPage(tempPage);
            task.fork();
            taskList.add(task);
        }
        taskList.forEach(ForkJoinTask::join);
        return page;
    }

    private HashSet<String> getLinks(String url) {
        HashSet<String> resultList = new HashSet<>();
        System.out.println("Парсим страницу по адресу " + url);
        try {
            Thread.sleep((int) (Math.random() * 1000) + 1000); // Случайная задержка от 1000 до 2000 мс
            String[] userAgentReferrer = generateRandomUserAgentReferrer();
            Document doc = Jsoup.connect(url)
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent(userAgentReferrer[0])
                    .referrer(userAgentReferrer[1])
                    .maxBodySize(1000_000_000)
                    .timeout(15_000)
                    .ignoreContentType(true)
                    .get();
            if (doc != null) {
                Elements elements = doc.select("a[href]");
                for (Element e : elements) {
                    String tempLink = e.attr("abs:href");
                    if (isLinkFitsUs(tempLink)) {
                        resultList.add(tempLink);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultList;
    }

    private boolean isLinkFitsUs(String link) {
        return link.startsWith(rootUrl) && !link.contains("index") && !link.endsWith(".pdf") && !link.contains("#")
                && !link.isEmpty() && !link.endsWith(".jpg") && !link.endsWith(".png")
                && !link.endsWith(".jpeg") && !link.endsWith(".doc") && !link.endsWith(".ico");
    }

    private String[] generateRandomUserAgentReferrer() {
        int randomUserAgent = (int) (Math.random() * 3);
        int randomReferrer = (int) (Math.random() * 3);
        return new String[]{USER_AGENT_LIST[randomUserAgent], REFERER_LIST[randomReferrer]};
    }
}