package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    // @Size(max = 3)
    //@Column(name = "      ", columnDefinition="TINYINT(3) UNSIGNED default '40'")
    @Column(name = "is_moderator", nullable = false, columnDefinition="TINYINT(1)")        // является ли пользователь модератором
    private boolean isModerator;            // (может ли править глобальные настройки сайта и модерировать посты)

    @Column(name = "reg_time", nullable = false, columnDefinition = "DATETIME")
    private Date registrationTime;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)", unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)", unique = true)
    private String email;

    // https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    // https://www.codota.com/code/java/methods/org.riotfamily.core.security.auth.HibernateUserDao/hashPassword
    // https://www.baeldung.com/java-password-hashing
    // https://askdev.ru/q/autentifikaciya-hibernate-bez-paroley-hranyaschihsya-v-obychnom-tekste-214168/
    @Column(name = "password", nullable = false, columnDefinition = "VARCHAR(255)")
    private String hashedPassword;

    @Column(nullable = true, columnDefinition = "VARCHAR(255)")
    private String code;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String photo;

}
