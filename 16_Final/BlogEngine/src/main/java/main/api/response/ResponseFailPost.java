package main.api.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseFailPost implements ResponseApi {

    private boolean result;
    private Map<String, String> errors;

    public ResponseFailPost(boolean isTextValid, boolean isTitleValid) {
        result = false;
        errors = new HashMap<>();
        if (!isTextValid) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        if (!isTitleValid) {
            errors.put("title", "Заголовок не установлен");
        }

    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
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
