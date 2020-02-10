import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Page {

    private String url;
    private CopyOnWriteArrayList<Page> subPagesList;
    private int pageLevel;

    public Page(String url, int pageLevel) {
        this.url = url;
        this.pageLevel = pageLevel;
    }

    public Page(String url, CopyOnWriteArrayList<Page> subPagesList) {
        this.url = url;
        this.subPagesList = subPagesList;
    }

    public int getPageLevel() {
        return pageLevel;
    }

    public void setPageLevel(int pageLevel) {
        this.pageLevel = pageLevel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CopyOnWriteArrayList<Page> getSubPagesList() {
        if (subPagesList == null) {
            return null;
        }
        return subPagesList;
    }

    public void setSubPagesList(CopyOnWriteArrayList<Page> subPagesList) {
        this.subPagesList = subPagesList;
    }

    public void addSubPage(Page page) {
        if (this.subPagesList == null) {
            this.subPagesList = new CopyOnWriteArrayList<>();
        }
        this.subPagesList.add(page);
    }
}