package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "global_settings")
public class GlobalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String value;
}

// id INT NOT NULL AUTO_INCREMENT
//● code VARCHAR(255) NOT NULL - системное имя настройки
//● name VARCHAR(255) NOT NULL - название настройки
//● value VARCHAR(255) NOT NULL - значение настройки

// MULTIUSER_MODE Многопользовательский режим YES / NO
//POST_PREMODERATION Премодерация постов YES / NO
//STATISTICS_IS_PUBLIC Показывать всем статистику блога YES / NO