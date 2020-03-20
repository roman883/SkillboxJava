package main.services.Impl;

import com.github.cage.Cage;
import com.github.cage.IGenerator;
import com.github.cage.ObjectRoulette;
import com.github.cage.image.EffectConfig;
import com.github.cage.image.Painter;
import com.github.cage.image.RgbColorGenerator;
import com.github.cage.token.RandomTokenGenerator;
import main.api.response.ResponseApi;
import main.model.entities.CaptchaCode;
import main.model.repositories.CaptchaRepository;
import main.api.response.ResponseCaptcha;
import main.services.interfaces.CaptchaRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CaptchaRepositoryServiceImpl implements CaptchaRepositoryService {

    private static final long OLD_CAPTCHA_DELETE_TIME_IN_MIN = 60; // TODO задавать через конфиг?
    private static final char[] SYMBOLS_FOR_GENERATOR = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int RANDOM_SECRET_KEY_SIZE = 22;
    private static final int CAPTCHA_PICTURE_SIZE_LIMIT = 5;
    private static final String CAPTCHA_FORMAT = "png";
    private static final String CAPTCHA_FORMAT_STRING = "data:image/png;base64, ";

    @Autowired
    private CaptchaRepository captchaRepository;

    @Override
    public ResponseEntity<ResponseApi> generateCaptcha() {
        ArrayList<CaptchaCode> captchas = getAllCaptchas(); // Сначала удаляем все устаревшие капчи
        for (CaptchaCode captcha : captchas) {
            LocalDateTime captchaCreatedTime = captcha.getTime().toLocalDateTime();
            LocalDateTime oldCaptchaTime = LocalDateTime.now().minusMinutes(OLD_CAPTCHA_DELETE_TIME_IN_MIN);
            if (captchaCreatedTime.isBefore(oldCaptchaTime)) { // капча устарела
                captchaRepository.delete(captcha);
            }
        }
        String secretCode = generateRandomString();
        Cage cage = getCage();
        String token = cage.getTokenGenerator().next();
        if (token.length() > CAPTCHA_PICTURE_SIZE_LIMIT) { // Ограничиваем размер картинки капчи символами
            token = token.substring(0, CAPTCHA_PICTURE_SIZE_LIMIT);
        }
        byte[] encodedBytes = Base64.getEncoder().encode(cage.draw(token));
        String captchaImageBase64String = CAPTCHA_FORMAT_STRING + new String(encodedBytes, StandardCharsets.UTF_8);
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
        for (int i = 0; i < RANDOM_SECRET_KEY_SIZE; i++) {
            builder.append(SYMBOLS_FOR_GENERATOR[(int) (Math.random() * (SYMBOLS_FOR_GENERATOR.length - 1))]);
        }
        return builder.toString();
    }

    private Cage getCage() {
        Random rnd = new Random();
        Painter painter = new Painter(
                103, 56, Color.WHITE, Painter.Quality.MAX, new EffectConfig(), rnd);
        int defFontHeight = painter.getHeight() / 2;
        return new Cage(
                painter,
                (IGenerator<Font>) new ObjectRoulette<>(rnd, new Font[]{new Font("SansSerif", Font.PLAIN, defFontHeight),
                        new Font("Serif", Font.PLAIN, defFontHeight), new Font("Monospaced", Font.BOLD, defFontHeight)}),
                (IGenerator<Color>) new RgbColorGenerator(rnd),
                CAPTCHA_FORMAT,
                Cage.DEFAULT_COMPRESS_RATIO,
                (IGenerator<String>) new RandomTokenGenerator(rnd),
                rnd);
    }
}
