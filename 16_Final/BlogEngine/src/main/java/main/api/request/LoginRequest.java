package main.api.request;

import java.io.Serializable;

public class LoginRequest implements RequestApi, Serializable {

    private String e_mail; //TODO Заменить везде на Java формат
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String e_mail, String password) {
        this.e_mail = e_mail;
        this.password = password;
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
}
