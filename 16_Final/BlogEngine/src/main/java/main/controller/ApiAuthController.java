package main.controller;

import main.model.responses.ResponseAPI;
import main.services.interfaces.CaptchaRepositoryService;
import main.services.Impl.CaptchaRepositoryServiceImpl;
import main.services.Impl.UserRepositoryServiceImpl;
import main.services.interfaces.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@ComponentScan("service")
public class ApiAuthController {

    private UserRepositoryService userRepoService;
    private CaptchaRepositoryService captchaRepoService;

    @Autowired
    public ApiAuthController(UserRepositoryServiceImpl userRepoServiceImpl,
                             CaptchaRepositoryServiceImpl captchaRepoServiceImpl) {
        this.userRepoService = userRepoServiceImpl;
        this.captchaRepoService = captchaRepoServiceImpl;
    }

    @GetMapping(value = "/api/auth/logout") // TODO вынести общую часть /api/auth/ в @RequestMapping класса
    public @ResponseBody
    ResponseEntity<ResponseAPI> logout(HttpServletRequest request) {
        return userRepoService.logout(request.getSession());
    }

    // Методы
    @PostMapping(value = "/api/auth/register", params = {"e_mail", "name", "password", "captcha", "captcha_secret"})
    public @ResponseBody
    ResponseEntity<ResponseAPI> register(@RequestParam(value = "e_mail") String email,
                               @RequestParam(value = "name") String name,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "captcha") String captcha,
                               @RequestParam(value = "captcha_secret") String captchaSecret
    ) {
        return userRepoService.register(email, name, password, captcha, captchaSecret);
    }

    @PostMapping(value = "/api/auth/login", params = {"e_mail", "password"})
    public @ResponseBody
    ResponseEntity<ResponseAPI> login(@RequestParam(value = "e_mail") String email,
                            @RequestParam(value = "password") String password, HttpServletRequest request) {
        return userRepoService.login(email, password, request.getSession());
    }

    @GetMapping(value = "/api/auth/check")
    public @ResponseBody
    ResponseEntity<ResponseAPI> checkAuth(HttpServletRequest request) {
        return userRepoService.checkAuth(request.getSession());
    }

    @PostMapping(value = "/api/auth/restore", params = {"email"})
    public @ResponseBody
    ResponseEntity<ResponseAPI> restorePassword(@RequestParam(value = "email") String email) {
        return userRepoService.restorePassword(email);
    }

    @PostMapping(value = "/api/auth/password", params = {"code", "password", "captcha", "captcha_secret"})
    public @ResponseBody
    ResponseEntity<ResponseAPI> changePassword(@RequestParam(value = "code") String code,
                                          @RequestParam(value = "password") String password,
                                          @RequestParam(value = "captcha") String captcha,
                                          @RequestParam(value = "captcha_secret") String captchaSecret) {
        return userRepoService.changePassword(code, password, captcha, captchaSecret);
    }

    @GetMapping(value = "/api/auth/captcha")
    public @ResponseBody
    ResponseEntity<ResponseAPI> generateCaptcha() {
        return captchaRepoService.generateCaptcha();
    }
}
