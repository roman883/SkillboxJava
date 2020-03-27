package main.api.response;

public class ResponseFailComment implements ResponseApi{

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

    static class ErrorsFailComment {

        private String text;

        private ErrorsFailComment() {
            text = "Текст комментария не задан, слишком короткий или превышает максимальный размер";
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
