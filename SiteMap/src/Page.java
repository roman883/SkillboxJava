import java.util.ArrayList;

public class Page {

    private String url;
    private ArrayList<Page> outerLinksList;
    private int pageLevel;

    public Page(String url, int pageLevel) {
        this.url = url;
        this.pageLevel = pageLevel;
    }

    public Page(String url, ArrayList<Page> outerLinksList) {
        this.url = url;
        this.outerLinksList = outerLinksList;
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

    public ArrayList<Page> getOuterLinksList() {
        if (outerLinksList == null) {
            return null;
        }
        return outerLinksList;
    }

    public void setOuterLinksList(ArrayList<Page> outerLinksList) {
        this.outerLinksList = outerLinksList;
    }

    public void addOuterLink(Page page) {
        if (this.outerLinksList == null) {
            this.outerLinksList = new ArrayList<>();
        }
        this.outerLinksList.add(page);
    }
}


