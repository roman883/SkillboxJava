package main.api.request;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

public class LoginRequest implements RequestApi, Serializable {

    private String e_mail; // TODO как получать поле в запросе "e_mail" и заполнять поле с именем "email"? Привязка
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
