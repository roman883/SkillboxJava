package main.model.DTOs;

import main.Errors;

public class ResultSignUpDTO {

    private boolean result;
    private Errors errors;

    public ResultSignUpDTO() {
        this.result = false;
        errors = Errors.getInstance();
    }

    public ResultSignUpDTO(boolean positiveResult) {
        result = true;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }
}
