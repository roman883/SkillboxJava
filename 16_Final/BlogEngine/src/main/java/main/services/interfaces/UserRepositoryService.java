package main.services.interfaces;

import main.model.DTOs.ResultLogoutDTO;
import main.model.entities.User;
import main.model.repositories.PostVoteRepository;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;

public interface UserRepositoryService {

    ResponseEntity<User> getUser(int id);

    ResponseEntity<?> login(String email, String password, PostRepositoryService postRepositoryService,
                            HttpSession session);

    ResponseEntity<String> checkAuth(HttpSession session, PostRepositoryService postRepositoryService);

    ResponseEntity<String> restorePassword(String email);

    ResponseEntity<String> changePassword(String code, String password, String captcha, String captcha_secret,
                                          CaptchaRepositoryService captchaRepositoryService);

    ResponseEntity<?> register(String email, String name, String password, String captcha, String captcha_secret);

    ResponseEntity<String> editProfile(File photo, Byte removePhoto, String name, String email, String password,
                                       HttpSession session);

    ResponseEntity<String> getMyStatistics(HttpSession session, PostVoteRepositoryService postVoteRepositoryService,
                                   PostRepositoryService postRepositoryService);

    ResponseEntity getAllStatistics(HttpSession session,
                                    GlobalSettingsRepositoryService globalSettingsRepositoryService,
                                    PostVoteRepositoryService postVoteRepositoryService,
                                    PostRepositoryService postRepositoryService);

    ResponseEntity<ResultLogoutDTO> logout(HttpSession session);

    Integer getUserIdBySession(HttpSession session);

    ArrayList<User> getAllUsersList();
}
