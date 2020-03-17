package main.model.responses;

public class ResponseFailChangePass implements ResponseAPI {

    private boolean result;
    private ErrorsAPI errors;

    public ResponseFailChangePass() {
        result = false;
        errors = new ErrorsAPI();
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsAPI getErrors() {
        return errors;
    }

    public void setErrors(ErrorsAPI errors) {
        this.errors = errors;
    }

    private static class ErrorsAPI {

        private String code;
        private String password;
        private String captcha;

        private ErrorsAPI() {
            code = "Ссылка для восстановления пароля устарела.\n" +
                    "<a href=\"/auth/restore\">Запросить ссылку снова</a>";
            password = "Пароль короче 6-ти символов";
            captcha = "Код с картинки введён неверно";
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }
    }
}
