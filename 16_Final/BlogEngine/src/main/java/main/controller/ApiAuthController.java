package main.controller;

import main.api.request.ChangePasswordRequest;
import main.api.request.RegisterRequest;
import main.api.request.LoginRequest;
import main.api.request.RestorePassRequest;
import main.api.response.ResponseApi;
import main.services.interfaces.CaptchaRepositoryService;
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

    @Autowired
    private UserRepositoryService userRepoService;
    @Autowired
    private CaptchaRepositoryService captchaRepoService;

    public ApiAuthController() {
    }

    @GetMapping(value = "logout") // TODO вынести общую часть /api/auth/ в @RequestMapping класса
    public ResponseEntity<ResponseApi> logout(HttpServletRequest request) {
        return userRepoService.logout(request.getSession());
    }

    @PostMapping(value = "register")
    public ResponseEntity<ResponseApi> register(@RequestBody RegisterRequest registerRequest) {
        return userRepoService.register(registerRequest);
    }

    @PostMapping(value = "login")
    public ResponseEntity<ResponseApi> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userRepoService.login(loginRequest, request.getSession());
    }

    @GetMapping(value = "check")
    public ResponseEntity<ResponseApi> checkAuth(HttpServletRequest request) {
        return userRepoService.checkAuth(request.getSession());
    }

    @PostMapping(value = "restore")
    public ResponseEntity<ResponseApi> restorePassword(@RequestBody RestorePassRequest restorePassRequest) {
        return userRepoService.restorePassword(restorePassRequest);
    }

    @PostMapping(value = "password")
    public ResponseEntity<ResponseApi> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return userRepoService.changePassword(changePasswordRequest);
    }

    @GetMapping(value = "captcha")
    public ResponseEntity<ResponseApi> generateCaptcha() {
        return captchaRepoService.generateCaptcha();
    }
}