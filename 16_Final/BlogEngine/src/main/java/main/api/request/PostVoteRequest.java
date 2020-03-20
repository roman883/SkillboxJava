package main.api.request;

public class PostVoteRequest implements RequestApi {

    private int post_id;

    public PostVoteRequest() {
    }

    public PostVoteRequest(int post_id) {
        this.post_id = post_id;
    }

    public int getPostId() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }
}
