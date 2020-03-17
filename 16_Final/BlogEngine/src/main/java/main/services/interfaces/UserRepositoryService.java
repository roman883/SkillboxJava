package main.services.interfaces;

import main.model.responses.ResponseAPI;
import main.model.entities.User;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;

public interface UserRepositoryService {

    ResponseEntity<User> getUser(int id);

    ResponseEntity<ResponseAPI> login(String email, String password, HttpSession session);

    ResponseEntity<ResponseAPI> checkAuth(HttpSession session);

    ResponseEntity<ResponseAPI> restorePassword(String email);

    ResponseEntity<ResponseAPI> changePassword(String code, String password, String captcha, String captcha_secret);

    ResponseEntity<ResponseAPI> register(String email, String name, String password, String captcha, String captcha_secret);

    ResponseEntity<ResponseAPI> editProfile(File photo, Byte removePhoto, String name, String email, String password,
                                       HttpSession session);

    ResponseEntity<?> getMyStatistics(HttpSession session);

    ResponseEntity<?> getAllStatistics(HttpSession session);

    ResponseEntity<ResponseAPI> logout(HttpSession session);

    Integer getUserIdBySession(HttpSession session);

    ArrayList<User> getAllUsersList();
}
