package main.api.request;

public class ChangePasswordRequest implements RequestApi {

    private String code;
    private String password;
    private String captcha;
    private String captcha_secret;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String code, String password, String captcha, String captcha_secret) {
        this.code = code;
        this.password = password;
        this.captcha = captcha;
        this.captcha_secret = captcha_secret;
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

    public String getCaptchaSecret() {
        return captcha_secret;
    }

    public void setCaptcha_secret(String captcha_secret) {
        this.captcha_secret = captcha_secret;
    }
}
