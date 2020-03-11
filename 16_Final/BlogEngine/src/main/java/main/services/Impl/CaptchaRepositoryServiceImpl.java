package main.services.Impl;

import main.model.entities.CaptchaCode;
import main.model.repositories.CaptchaRepository;
import main.services.interfaces.CaptchaRepositoryService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class CaptchaRepositoryServiceImpl implements CaptchaRepositoryService {

    private final static long OLD_CAPTCHA_DELETE_TIME_IN_MIN = 60; // TODO задавать через конфиг
    @Autowired
    private CaptchaRepository captchaRepository;

    @Override
    public ResponseEntity<String> generateCaptcha() {
        ArrayList<CaptchaCode> captchas = getAllCaptchas(); // Сначала удаляем все устаревшие капчи
        for (CaptchaCode captcha : captchas) {
            LocalDateTime captchaCreatedTime = captcha.getTime().toLocalDateTime();
            LocalDateTime oldCaptchaTime = LocalDateTime.now().minusMinutes(OLD_CAPTCHA_DELETE_TIME_IN_MIN);
            if (captchaCreatedTime.isBefore(oldCaptchaTime)) { // капча устарела
                captchaRepository.delete(captcha);
            }
        }
        // TODO код для генерации секретного кода капч
        String secret = Integer.toString((Double.toString(
                Math.pow((Math.random() * Math.random()), 100 * Math.random())
        )).hashCode());
        // TODO грузить картинку как-то и код из картинки!
        String captchaPictureCode = "картинка капчи в base64";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("secret", secret).put("image", captchaPictureCode);
        CaptchaCode newCaptcha = new CaptchaCode(Timestamp.valueOf(LocalDateTime.now()), captchaPictureCode, secret);
        captchaRepository.save(newCaptcha);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    public ArrayList<CaptchaCode> getAllCaptchas() {
        ArrayList<CaptchaCode> captchaCodes = new ArrayList<>();
        captchaRepository.findAll().forEach(captchaCodes::add);
        return captchaCodes;
    }
}
