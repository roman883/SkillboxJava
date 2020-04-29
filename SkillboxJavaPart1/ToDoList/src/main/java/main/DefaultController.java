// NOT USED

//package main;
//
//import main.model.Task;
//import main.model.TaskRepository;
//import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.ArrayList;
//
//@Controller
//public class DefaultController {
//
////    @Value("${someParameter.value}")
////    private String someParameter;
//
//    @Autowired
//    TaskRepository taskRepository;
//
//    @RequestMapping("/")
//    public String index(Model model) {
//
//        ArrayList<Task> tasks = new ArrayList<Task>();
//        taskRepository.findAll().forEach(tasks::add);
//        model.addAttribute("tasks", tasks);
//        model.addAttribute("tasksCount", tasks.size());
////        model.addAttribute("someParameter", someParameter);
//            return "index";
//
//    }
//}