package main.api.request;

public class ModeratePostRequest implements RequestApi {

    private Integer post_id;
    private String decision;

    public ModeratePostRequest(Integer post_id, String decision) {
        this.post_id = post_id;
        this.decision = decision;
    }

    public ModeratePostRequest() {
    }

    public Integer getPostId() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
