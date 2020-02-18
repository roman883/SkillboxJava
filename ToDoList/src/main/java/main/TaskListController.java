package main;

import main.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.RepositoryService;

import java.util.ArrayList;


@Controller
@ComponentScan("service")
public class TaskListController {

    private final RepositoryService repositoryService;

    @Autowired
    public TaskListController (RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @DeleteMapping("/tasks/")
    public ResponseEntity<String> delete() {
        return repositoryService.deleteAllTasks();
    }

    @RequestMapping("/")
    public String index(Model model) {
        ArrayList<Task> tasks = (ArrayList<Task>) repositoryService.getAllTasks().getBody();
        model.addAttribute("tasks", tasks);
        model.addAttribute("tasksCount", tasks.size());
//        model.addAttribute("someParameter", someParameter);
        return "index";
    }
}

