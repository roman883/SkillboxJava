package main.services.interfaces;

import main.model.entities.CaptchaCode;
import main.model.responses.ResponseAPI;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface CaptchaRepositoryService {

    ResponseEntity<ResponseAPI> generateCaptcha();

    ArrayList<CaptchaCode> getAllCaptchas();
}
