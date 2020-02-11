package main;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.Task;

import java.util.List;

@RestController
public class TaskController {

    @GetMapping(value = "/tasks/") // @RequestMapping(value = "/tasks/", method = RequestMethod.GET)
    public List<Task> list() {
        return TaskList.getAllTasks();
    }

    @PostMapping(value = "/tasks/")
    public int add(Task task) {
        return TaskList.addTask(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity get(@PathVariable int id) {
        Task task = TaskList.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @PutMapping(value = "/tasks/{id}", params = {"name","description"})
    public @ResponseBody ResponseEntity put(
            @PathVariable int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description) {
        Task task = TaskList.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        task.setDescription(description);
        task.setName(name);
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @PatchMapping(value = "/tasks/{id}", params = {"name","description"})
    public @ResponseBody ResponseEntity patch(
            @PathVariable int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description) {
        Task task = TaskList.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        else {
            if(!description.equals("")) {
                task.setDescription(description);
            }
            if (!name.equals("")) {
                task.setName(name);
            }
        }
        return new ResponseEntity(task, HttpStatus.OK);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        if (TaskList.deleteTask(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}