package main.controller;

import main.api.request.ChangePasswordRequest;
import main.api.request.RegisterRequest;
import main.api.request.LoginRequest;
import main.api.request.RestorePassRequest;
import main.api.response.ResponseApi;
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
@RequestMapping(value = "/api/auth/")
public class ApiAuthController {

    private UserRepositoryService userRepoService;
    private CaptchaRepositoryService captchaRepoService;

    @Autowired
    public ApiAuthController(UserRepositoryServiceImpl userRepoServiceImpl,
                             CaptchaRepositoryServiceImpl captchaRepoServiceImpl) {
        this.userRepoService = userRepoServiceImpl;
        this.captchaRepoService = captchaRepoServiceImpl;
    }

    @GetMapping(value = "logout") // TODO вынести общую часть /api/auth/ в @RequestMapping класса
    public @ResponseBody  //TODO remove all annotations '@ResponseBody': @RestController is a composed annotation that
        // is itself meta-annotated with @Controller and @ResponseBody to indicate a controller whose every method
        // inherits the type-level @ResponseBody annotation and, therefore, writes directly to the response body versus
        // view resolution and rendering with an HTML template.
    ResponseEntity<ResponseApi> logout(HttpServletRequest request) {
        return userRepoService.logout(request.getSession());
    }

    // Методы
    @PostMapping(value = "register") //params = {"e_mail", "name", "password", "captcha", "captcha_secret"}
    public @ResponseBody
    ResponseEntity<ResponseApi> register(@RequestBody RegisterRequest registerRequest) {
        return userRepoService.register(registerRequest);
    }

    @PostMapping(value = "login") // params = {"e_mail", "password"}
    public @ResponseBody
    ResponseEntity<ResponseApi> login(@RequestBody LoginRequest loginRequest
//            @RequestParam(value = "e_mail") String email,
//                            @RequestParam(value = "password") String password
            , HttpServletRequest request) {
        return userRepoService.login(loginRequest, request.getSession());
    }

    @GetMapping(value = "check")
    public @ResponseBody
    ResponseEntity<ResponseApi> checkAuth(HttpServletRequest request) {
        return userRepoService.checkAuth(request.getSession());
    }

    @PostMapping(value = "restore")
    public @ResponseBody
    ResponseEntity<ResponseApi> restorePassword(@RequestBody RestorePassRequest restorePassRequest) {
        return userRepoService.restorePassword(restorePassRequest);
    }

    @PostMapping(value = "password")
    public @ResponseBody
    ResponseEntity<ResponseApi> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return userRepoService.changePassword(changePasswordRequest);
    }

    @GetMapping(value = "captcha")
    public @ResponseBody
    ResponseEntity<ResponseApi> generateCaptcha() {
        return captchaRepoService.generateCaptcha();
    }
}
