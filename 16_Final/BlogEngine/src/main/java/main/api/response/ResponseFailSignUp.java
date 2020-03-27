package main.api.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseFailSignUp implements ResponseApi {

    private boolean result;
    private Map<String, String> errors;

    public ResponseFailSignUp(boolean isNameValid, boolean isPassValid, boolean isCaptchaCodeValid, boolean isEmailValid) {
        errors = new HashMap<>();
        if (!isNameValid) errors.put("name", "Имя указано неверно");
        if (!isPassValid) errors.put("password", "Пароль короче 6-ти символов");
        if (!isCaptchaCodeValid) errors.put("captcha", "Код с картинки введён неверно");
        if (!isEmailValid) errors.put("email", "Этот e-mail уже зарегистрирован");
        result = false;
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

    //    public Errors getErrors() {
//        return errors;
//    }
//
//    public void setErrors(Errors errors) {
//        this.errors = errors;
//    }
//
//    static class Errors {
//
//        private String email = "Этот e-mail уже зарегистрирован";
//        private String name = "Имя указано неверно";
//        private String password = "Пароль короче 6-ти символов";
//        private String captcha = "Код с картинки введён неверно";
//
//        private static volatile Errors instance; // Volatile для многопоточности
//
//        private Errors() {
//        }
//
//        public static Errors getInstance() { // создание только при обращении
//            if (instance == null) {
//                synchronized (Errors.class) {
//                    if (instance == null) { // если объект не создан, то создаем
//                        instance = new Errors();
//                    }
//                }
//            }
//            return instance;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getPassword() {
//            return password;
//        }
//
//        public void setPassword(String password) {
//            this.password = password;
//        }
//
//        public String getCaptcha() {
//            return captcha;
//        }
//
//        public void setCaptcha(String captcha) {
//            this.captcha = captcha;
//        }
//    }
}
