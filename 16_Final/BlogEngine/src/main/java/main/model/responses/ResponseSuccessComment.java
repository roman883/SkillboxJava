package main.model.responses;

import main.model.entities.PostComment;

public class ResponseSuccessComment implements ResponseAPI {

    private int id;

    public ResponseSuccessComment(PostComment postComment) {
        id = postComment.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
