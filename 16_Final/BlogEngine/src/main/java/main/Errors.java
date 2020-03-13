package main;

// или удалить, используется только один раз
public class Errors {

    private String email = "Этот e-mail уже зарегистрирован";
    private String name = "Имя указано неверно";
    private String password = "Пароль короче 6-ти символов";
    private String captcha = "Код с картинки введён неверно";

    private static volatile Errors instance; // Volatile для многопоточности

    private Errors() {
    }

    public static Errors getInstance() { // создание только при обращении
        if (instance == null) {
            synchronized (Errors.class) {
                if (instance == null) { // если объект не создан, то создаем
                    instance = new Errors();
                }
            }
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
