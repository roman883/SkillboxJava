package main.controller;

import main.model.DTOs.ResultLogoutDTO;
import main.services.interfaces.CaptchaRepositoryService;
import main.services.Impl.CaptchaRepositoryServiceImpl;
import main.services.Impl.UserRepositoryServiceImpl;
import main.services.interfaces.PostRepositoryService;
import main.services.Impl.PostRepositoryServiceImpl;
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
    private PostRepositoryService postRepoService;

    @Autowired
    public ApiAuthController(UserRepositoryServiceImpl userRepoServiceImpl,
                             CaptchaRepositoryServiceImpl captchaRepoServiceImpl,
                             PostRepositoryServiceImpl postRepoServiceImpl) {
        this.userRepoService = userRepoServiceImpl;
        this.captchaRepoService = captchaRepoServiceImpl;
        this.postRepoService = postRepoServiceImpl;
    }

    @GetMapping(value = "/api/auth/logout") // TODO вынести общую часть /api/auth/ в @RequestMapping класса
    public @ResponseBody
    ResponseEntity<ResultLogoutDTO> logout(HttpServletRequest request) {
        return userRepoService.logout(request.getSession());
    }

    // Методы
    @PostMapping(value = "/api/auth/register", params = {"e_mail", "name", "password", "captcha", "captcha_secret"})
    public @ResponseBody
    ResponseEntity<?> register(@RequestParam(value = "e_mail") String email,
                               @RequestParam(value = "name") String name,
                               @RequestParam(value = "password") String password,
                               @RequestParam(value = "captcha") String captcha,
                               @RequestParam(value = "captcha_secret") String captchaSecret
    ) {
        return userRepoService.register(email, name, password, captcha, captchaSecret);
    }

    @PostMapping(value = "/api/auth/login", params = {"email", "password"})
    public @ResponseBody
    ResponseEntity<?> login(@RequestParam(value = "email") String email,
                            @RequestParam(value = "password") String password, HttpServletRequest request) {
        return userRepoService.login(email, password, postRepoService, request.getSession());
    }

    @GetMapping(value = "/api/auth/check")
    public @ResponseBody
    ResponseEntity<String> checkAuth(HttpServletRequest request) {
        return userRepoService.checkAuth(request.getSession(), postRepoService);
    }

    @PostMapping(value = "/api/auth/restore", params = {"email"})
    public @ResponseBody
    ResponseEntity<String> restorePassword(@RequestParam(value = "email") String email) {
        return userRepoService.restorePassword(email);
    }

    @PostMapping(value = "/api/auth/password", params = {"code", "password", "captcha", "captcha_secret"})
    public @ResponseBody
    ResponseEntity<String> changePassword(@RequestParam(value = "code") String code,
                                          @RequestParam(value = "password") String password,
                                          @RequestParam(value = "captcha") String captcha,
                                          @RequestParam(value = "captcha_secret") String captchaSecret) {
        return userRepoService.changePassword(code, password, captcha, captchaSecret, captchaRepoService);
    }

    @GetMapping(value = "/api/auth/captcha")
    public @ResponseBody
    ResponseEntity<String> generateCaptcha() {
        return captchaRepoService.generateCaptcha();
    }
}
