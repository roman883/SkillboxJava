package main.model.responses;

import java.util.HashMap;
import java.util.Map;

public class ResponseFailEditProfile implements ResponseAPI {

    private boolean result;
    private Map<String, String> errors;

    public ResponseFailEditProfile() {
        result = false;
        errors = new HashMap<>();
        errors.put("email", "Этот e-mail уже зарегистрирован");
        errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
        errors.put("name", "Имя указано неверно");
        errors.put("password", "Пароль короче 6-ти символов");
        errors.put("captcha", "Код с картинки введён неверно");
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
}
