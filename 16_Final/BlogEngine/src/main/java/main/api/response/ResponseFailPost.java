package main.api.response;

public class ResponseFailPost implements ResponseApi {

    private boolean result;
    private FailPostErrors errors;

    public ResponseFailPost() {
        result = false;
        errors = new FailPostErrors();
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public FailPostErrors getErrors() {
        return errors;
    }

    public void setErrors(FailPostErrors errors) {
        this.errors = errors;
    }

    static class FailPostErrors {
        private String title;
        private String text;

        private FailPostErrors() {
            title = "Заголовок не установлен";
            text = "Текст публикации слишком короткий";
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
