package main.services.Impl;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.model.entities.CaptchaCode;
import main.model.repositories.CaptchaRepository;
import main.model.responses.ResponseAPI;
import main.model.responses.ResponseCaptcha;
import main.services.interfaces.CaptchaRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CaptchaRepositoryServiceImpl implements CaptchaRepositoryService {

    private static final Cage cage = new GCage();
    private final static long OLD_CAPTCHA_DELETE_TIME_IN_MIN = 60; // TODO задавать через конфиг
    private static final char[] SYMBOLS_FOR_GENERATOR = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int KEY_SIZE = 22;
    @Autowired
    private CaptchaRepository captchaRepository;

    @Override
    public ResponseEntity<ResponseAPI> generateCaptcha() {
        ArrayList<CaptchaCode> captchas = getAllCaptchas(); // Сначала удаляем все устаревшие капчи
        for (CaptchaCode captcha : captchas) {
            LocalDateTime captchaCreatedTime = captcha.getTime().toLocalDateTime();
            LocalDateTime oldCaptchaTime = LocalDateTime.now().minusMinutes(OLD_CAPTCHA_DELETE_TIME_IN_MIN);
            if (captchaCreatedTime.isBefore(oldCaptchaTime)) { // капча устарела
                captchaRepository.delete(captcha);
            }
        }
        String secretCode = generateRandomString();
        String token = cage.getTokenGenerator().next();
        byte[] encodedBytes = Base64.getEncoder().encode(cage.draw(token));
        String captchaImageBase64String = new String(encodedBytes, StandardCharsets.UTF_8);
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        CaptchaCode newCaptcha = captchaRepository.save(new CaptchaCode(timestamp, token, secretCode));
        return new ResponseEntity<>(new ResponseCaptcha(secretCode, captchaImageBase64String), HttpStatus.OK);
    }

    public ArrayList<CaptchaCode> getAllCaptchas() {
        ArrayList<CaptchaCode> captchaCodes = new ArrayList<>();
        captchaRepository.findAll().forEach(captchaCodes::add);
        return captchaCodes;
    }

    private String generateRandomString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < KEY_SIZE; i++) {
            builder.append(SYMBOLS_FOR_GENERATOR[(int) (Math.random() * (SYMBOLS_FOR_GENERATOR.length - 1))]);
        }
        return builder.toString();
    }
}
