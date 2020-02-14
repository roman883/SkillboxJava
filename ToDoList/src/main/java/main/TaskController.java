package main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import main.model.Task;
import service.RepositoryService;

@RestController
@ComponentScan("service")
public class TaskController {

    private final RepositoryService repositoryService;

    @Autowired
    public TaskController (RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping(value = "/tasks/") // @RequestMapping(value = "/tasks/", method = RequestMethod.GET)
    public ResponseEntity list() {
        return repositoryService.getAllTasks();
    }

    @PostMapping(value = "/tasks/")
    public ResponseEntity add(Task task) {
        return repositoryService.addTask(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity get(@PathVariable int id) {
       return repositoryService.getTask(id);
    }

    @PutMapping(value = "/tasks/{id}", params = {"name", "description"})
    public @ResponseBody ResponseEntity put(
            @PathVariable int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description) {
            return repositoryService.putTask(id, name, description);
    }

    @PatchMapping(value = "/tasks/{id}", params = {"name", "description"})
    public @ResponseBody
    ResponseEntity patch(
            @PathVariable int id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description) {
        return repositoryService.patchTask(id, name, description);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        return repositoryService.deleteTask(id);
    }
}