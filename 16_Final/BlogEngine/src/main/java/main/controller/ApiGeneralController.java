package main.controller;

import main.model.Post;
import main.model.User;
import main.service.PostRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import main.service.UserRepositoryService;

import java.util.List;

@RestController
@ComponentScan("service")
public class ApiGeneralController {

    private final UserRepositoryService userRepositoryService;
    private final PostRepositoryService postRepositoryService;

    @Autowired
    public ApiGeneralController (UserRepositoryService userRepositoryService,
                                 PostRepositoryService postRepositoryService) {
        this.userRepositoryService = userRepositoryService;
        this.postRepositoryService = postRepositoryService;
    }

    @GetMapping("/{string}/{id}")
    public ResponseEntity<?> get(@PathVariable int id,
                                    @PathVariable String string) {
        if (string.equals("users")) {
            return userRepositoryService.getUser(id);
        } else if (string.equals("posts")) {
            return postRepositoryService.getPost(id);
        }
        return null;
    }

//    @GetMapping("/posts/")
//    public ResponseEntity<List<Post>> get() { return postRepositoryService.getAllPosts();}


}
