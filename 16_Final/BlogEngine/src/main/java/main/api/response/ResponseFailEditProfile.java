package main.api.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseFailEditProfile implements ResponseApi {

    private boolean result;
    private Map<String, String> errors;

    public ResponseFailEditProfile(boolean isEmailValid, boolean isPhotoValid, boolean isNameValid,
                                   boolean sPassValid, boolean isCaptchaCodeValid) {
        result = false;
        errors = new HashMap<>();
        if (!isEmailValid) errors.put("email", "Этот e-mail уже зарегистрирован");
        if (!isPhotoValid) errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
        if (!isNameValid) errors.put("name", "Имя указано неверно");
        if (!sPassValid) errors.put("password", "Пароль короче 6-ти символов");
        if (!isCaptchaCodeValid) errors.put("captcha", "Код с картинки введён неверно");
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
