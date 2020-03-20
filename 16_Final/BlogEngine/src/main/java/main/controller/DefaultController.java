package main.controller;

import main.api.response.ResponseApi;
import main.services.interfaces.PostRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefaultController
{
    @Autowired
    PostRepositoryService postRepoService;

    @RequestMapping("/")
    public String index(Model model)
    {
        return "index";
    }
}