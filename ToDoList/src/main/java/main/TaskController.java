package main;

import main.model.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import main.model.Task;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping(value = "/tasks/") // @RequestMapping(value = "/tasks/", method = RequestMethod.GET)
    public List<Task> list() {
        ArrayList<Task> tasks = new ArrayList<>();
        taskRepository.findAll().forEach(tasks::add);
        return tasks;
    }

    @PostMapping(value = "/tasks/")
    public int add(Task task) {
        Task tempTask;
        synchronized (taskRepository) {
            tempTask = taskRepository.save(task);
        }
        return tempTask.getId();
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity get(@PathVariable int id) {
        try {
            Task task = taskRepository.findById(id).orElseThrow(IllegalArgumentException::new);
            return new ResponseEntity(task, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping(value = "/tasks/{id}", params = {"name", "description"})
    public @ResponseBody
    ResponseEntity put(
            @PathVariable int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description) {
        try {
            Task task;
            synchronized (taskRepository) {
                task = taskRepository.findById(id).orElseThrow(IllegalArgumentException::new);
                task.setDescription(description);
                task.setName(name);
                taskRepository.save(task);
            }
            return new ResponseEntity(task, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping(value = "/tasks/{id}", params = {"name", "description"})
    public @ResponseBody
    ResponseEntity patch(
            @PathVariable int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description) {
        try {
            Task task;
            synchronized (taskRepository) {
                task = taskRepository.findById(id).orElseThrow(IllegalArgumentException::new);
                if (!description.equals("")) {
                    task.setDescription(description);
                }
                if (!name.equals("")) {
                    task.setName(name);
                }
                taskRepository.save(task);
            }
            return new ResponseEntity(task, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        synchronized (taskRepository) {
            if (taskRepository.existsById(id)) {
                taskRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}