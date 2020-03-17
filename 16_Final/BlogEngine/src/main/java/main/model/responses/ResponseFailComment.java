package main.model.responses;

public class ResponseFailComment implements ResponseAPI{

    private boolean result;
    private ErrorsFailComment errors;

    public ResponseFailComment() {
        result = false;
        errors = new ErrorsFailComment();
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsFailComment getErrors() {
        return errors;
    }

    public void setErrors(ErrorsFailComment errors) {
        this.errors = errors;
    }

    private static class ErrorsFailComment {

        private String text;

        private ErrorsFailComment() {
            text = "Текст комментария не задан или слишком короткий";
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
