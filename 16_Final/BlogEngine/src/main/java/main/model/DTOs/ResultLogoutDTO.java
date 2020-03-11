package main.model.DTOs;

public class ResultLogoutDTO {

    private boolean result;

    public ResultLogoutDTO() {
        this.result = false;
    }

    public ResultLogoutDTO(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
