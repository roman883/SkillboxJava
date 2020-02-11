package response;
import java.time.LocalDateTime;

public class Task {

    private int id;
    private String name;
    //  private Author author;
    //    private LocalDateTime dateAdded;
    //    private LocalDateTime datePlannedToDo;
    //    private Boolean isDone;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // У задачи - ID,
// название задачи,
// автор задачи
// дата создания,
// планируемая дата выполнения,
// выполнена ли задача,
// текст (описание) задачи
}
