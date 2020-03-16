package main.model.responses;

public class PostCommentAPI {

    private int id;
    private String time;
    private PostCommentAuthorAPI user;
    private String text;

    public PostCommentAPI(int id, String time, PostCommentAuthorAPI user, String text) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public PostCommentAuthorAPI getUser() {
        return user;
    }

    public void setUser(PostCommentAuthorAPI user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
