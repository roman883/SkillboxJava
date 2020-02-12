package main;

import main.model.Task;
import main.model.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;

@Controller
public class DefaultController {

    @Autowired
    TaskRepository taskRepository;

    @RequestMapping("/")
    public String index(Model model) {

        ArrayList<Task> tasks = new ArrayList<Task>();
        taskRepository.findAll().forEach(tasks::add);
        model.addAttribute("tasks", tasks);
        model.addAttribute("tasksCount", tasks.size());
            return "index";

    }
}