package main.api.response;

public class ResponseBoolean implements ResponseApi {

    private boolean result;

    public ResponseBoolean() {
        this.result = false;
    }

    public ResponseBoolean(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
