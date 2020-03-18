package main.api.request;

public class PostRequest implements RequestApi {

    private String time;
    private Byte active;
    private String title;
    private String text;
    private String[] tags;

    public PostRequest(String time, byte active, String title, String text, String[] tags) {
        this.time = time;
        this.active = active;
        this.title = title;
        this.text = text;
        this.tags = tags;
    }

    public PostRequest() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
