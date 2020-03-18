package main.api.request;

public class AddCommentRequest implements RequestApi {

    private Integer parent_id;
    private Integer post_id;
    private String text;

    public AddCommentRequest() {
    }

    public AddCommentRequest(Integer parent_id, Integer post_id, String text) {
        this.parent_id = parent_id;
        this.post_id = post_id;
        this.text = text;
    }

    public Integer getParentId() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getPostId() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
