package main;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class DefaultController {

    @RequestMapping("/")
    public String index() {
        if (Math.random() > 0.5) {
            return "Date is: " + new Date().toString();
        }
        else {
            return "Random number: " + (int) (Math.random() * 100);
        }
    }
}