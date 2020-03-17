package main.model.responses;

public class ResponseBoolean implements ResponseAPI {

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
