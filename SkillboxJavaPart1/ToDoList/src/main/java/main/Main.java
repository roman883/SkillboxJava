package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }
}






//  ______________________________________________________
//  | URL      |    MEthod |    Description |
//  /jobs/          GET         Список всех задач
//  /jobs/ID        GET         Получение данных о конкретной задаче
//  /jobs/          POST        Добавление задачи (в запросе параметры)
//  /jobs/ID        PUT         Сохранение изменений задачи целиком (все поля в запросе)
//  /jobs/ID        PATCH       Сохранение изменений отдельных свойств (в запросе отдельные поля)
//  /jobs/ID        DELETE      Удаление задачи (ИД)


// У задачи - ID,
// название задачи,
// автор задачи
// дата создания,
// планируемая дата выполнения,
// выполнена ли задача,
// текст (описание) задачи

// у автора задачи
// ID
// Name
// список задач автора

//Список задач

// список авторов