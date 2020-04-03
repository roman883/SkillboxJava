package main.api.response;

public class ResponseBadReqMsg implements ResponseApi {

    private String message;

    public ResponseBadReqMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
