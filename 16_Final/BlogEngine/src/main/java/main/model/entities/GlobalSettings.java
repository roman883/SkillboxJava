package main.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "global_settings")
public class GlobalSettings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public GlobalSettings(String code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public GlobalSettings() {
    }
}

// id INT NOT NULL AUTO_INCREMENT
//● code VARCHAR(255) NOT NULL - системное имя настройки
//● name VARCHAR(255) NOT NULL - название настройки
//● value VARCHAR(255) NOT NULL - значение настройки

// MULTIUSER_MODE Многопользовательский режим YES / NO
//POST_PREMODERATION Премодерация постов YES / NO
//STATISTICS_IS_PUBLIC Показывать всем статистику блога YES / NO