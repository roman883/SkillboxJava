package main.api.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseFailChangePass implements ResponseApi {

    private boolean result;
    private Map<String, String> errors;

    public ResponseFailChangePass(boolean isCodeValid, boolean isPassValid, boolean isCaptchaCodeValid) {
        errors = new HashMap<>();
        if (!isCodeValid) errors.put("code", "Ссылка для восстановления пароля устарела.\n" +
                "<a href=\"/auth/restore\">Запросить ссылку снова</a>");
        if(!isPassValid) errors.put("password", "Пароль короче 6-ти символов");
        if(!isCaptchaCodeValid) errors.put("captcha", "Код с картинки введён неверно");
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

    //    public ErrorsAPI getErrors() {
//        return errors;
//    }
//
//    public void setErrors(ErrorsAPI errors) {
//        this.errors = errors;
//    }

//    static class ErrorsAPI {
//
//        private String code;
//        private String password;
//        private String captcha;
//
//        private ErrorsAPI() {
//            code = "Ссылка для восстановления пароля устарела.\n" +
//                    "<a href=\"/auth/restore\">Запросить ссылку снова</a>";
//            password = "Пароль короче 6-ти символов";
//            captcha = "Код с картинки введён неверно";
//        }
//
//        public String getCode() {
//            return code;
//        }
//
//        public void setCode(String code) {
//            this.code = code;
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
