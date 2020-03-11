package main.services.interfaces;

import main.model.entities.CaptchaCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface CaptchaRepositoryService {

    ResponseEntity<String> generateCaptcha();

    ArrayList<CaptchaCode> getAllCaptchas();
}
