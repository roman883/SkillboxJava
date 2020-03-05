package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private Date time;

    @Column(nullable = false, columnDefinition = "TINYTEXT")
    private String code;

    @Column(name = "secret_code", nullable = false, columnDefinition = "TINYTEXT")
    private String secretCode;
}

// id INT NOT NULL AUTO_INCREMENT
//● time DATETIME NOT NULL - дата и время генерации кода капчи
//● code TINYTEXT NOT NULL - код, отображаемый на картинкке капчи
//● secret_code TINYTEXT NOT NULL - код, передаваемый в параметре