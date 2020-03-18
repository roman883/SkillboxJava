package main.api.request;

import java.io.Serializable;

public class RegisterRequest implements RequestApi, Serializable {

    private String e_mail;
//    private String name; // При регистрации не задается! Приравняем к E-mail
    private String password;
    private String captcha;
    private String captcha_secret;

    public RegisterRequest() {
    }

    public RegisterRequest(String e_mail, String password, String captcha, String captcha_secret) {
        this.e_mail = e_mail;
        this.password = password;
        this.captcha = captcha;
        this.captcha_secret = captcha_secret;
    }

    public String getEmail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
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
