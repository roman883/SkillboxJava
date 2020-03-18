package main.services.Impl;

import main.api.request.*;
import main.api.response.*;
import main.model.entities.*;
import main.model.repositories.UserRepository;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {

    private static final char[] SYMBOLS_FOR_GENERATOR = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int KEY_SIZE = 45;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaRepositoryService captchaRepositoryService;
    @Autowired
    private GlobalSettingsRepositoryService globalSettingsRepositoryService;
    @Autowired
    private PostVoteRepositoryService postVoteRepositoryService;
    @Autowired
    private PostRepositoryService postRepositoryService;

    private Map<String, Integer> sessionIdToUserId = new HashMap<>(); // Храним сессию и ID пользователя, по заданию не в БД

    @Override
    public ResponseEntity<User> getUser(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.isPresent() ?
                new ResponseEntity<>(optionalUser.get(), HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @Override
    public ResponseEntity<ResponseApi> login(LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        User user = null;
        ArrayList<User> userList = getAllUsersList();
        for (User u : userList) {
            if (u.getEmail().equals(email)) {
                if (u.getHashedPassword().equals(Integer.toString(password.hashCode()))) { // пароль совпал по хэшу
                    // TODO сделать проверку наличия сессии в базе
                    sessionIdToUserId.put(session.getId().toString(), u.getId()); // Запоминаем пользователя и сессию
                    user = u;
                    break;
                } else {
                    return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
                }
            }
        }
        if (user != null) {
            return new ResponseEntity<>(new ResponseLogin(user), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ResponseApi> checkAuth(HttpSession session) {
        if (!sessionIdToUserId.containsKey(session.getId().toString())) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.UNAUTHORIZED);
        } else {
            int userId = sessionIdToUserId.get(session.getId().toString());
            User user = getUser(userId).getBody();
            if (user != null) {
                return new ResponseEntity<>(new ResponseLogin(user), HttpStatus.OK);
            } else {        // "Ошибка! Пользователь найден в сессиях, однако отсутствует в БД!";
                return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @Override
    public ResponseEntity<ResponseApi> restorePassword(RestorePassRequest restorePassRequest) {
        String email = restorePassRequest.getEmail();
        ArrayList<User> userList = getAllUsersList();
        for (User user : userList) {
            if (user.getEmail().equals(email)) { // пользователь найден в базе
                String hash = generateRandomString();
                user.setCode(hash); // запоминаем код в базе
                userRepository.save(user);
                String link = "/login/change-password/" + hash;
                // TODO отправка ссылки на email пользователя!! И Занесение в БД к юзеру! А также в капчу  /login/change-password/HASH
                return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ResponseApi> changePassword(ChangePasswordRequest changePasswordRequest) {
        String code = changePasswordRequest.getCode();
        String password = changePasswordRequest.getPassword();
        String captcha = changePasswordRequest.getCaptcha();
        String captchaSecret = changePasswordRequest.getCaptchaSecret();
        // TODO реализовать проверку длины пароля и наличия букв/цифр/символов в пароле во всех методах
        if (code == null || password == null || captcha == null || captchaSecret == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        ArrayList<User> userList = getAllUsersList();
        for (User user : userList) {
            if (user.getCode().equals(code)) { // пользователь найден в базе по коду
                ArrayList<CaptchaCode> captchaCodes = captchaRepositoryService.getAllCaptchas();
                for (CaptchaCode captchaCode : captchaCodes) {
                    if (captchaCode.getSecretCode().equals(captchaSecret)) { // Нашли по секретному коду и проверяем введенные данные капчи
                        if (captchaCode.getCode().equals(captcha)) {
                            String newHashedPassword = Integer.toString(password.hashCode());
                            user.setHashedPassword(newHashedPassword);
                            return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
                        }
                    }
                }
            }
        }
        return new ResponseEntity<>(new ResponseFailChangePass(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ResponseApi> register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String name = email.replaceAll("@.+", ""); //TODO кнопка регистрации неактивна в шаблоне/JS, а также при регистрации не задается имя
        String password = registerRequest.getPassword();
        String captcha = registerRequest.getCaptcha();
        String captchaSecret = registerRequest.getCaptchaSecret();
        if (email.equals("") || name.equals("") || password.equals("") || captcha.equals("") || captchaSecret.equals("")) { // TODO проверки сделать на длину пароля, формат Email
            return new ResponseEntity<>(new ResponseFailSignUp(), HttpStatus.BAD_REQUEST);
        } else {    // если проверки прошли, создаем пользователя и возвращаем положительный результат
            Timestamp registrationDateTime = Timestamp.valueOf(LocalDateTime.now());
            String hashedPassword = Integer.toString(password.hashCode());
            User user = new User(false, registrationDateTime, name, email, hashedPassword);
            userRepository.save(user); // И можно получить id
            return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);  //TODO Может быть добавить сессию зарегинного юзера в sessionToUserId, чтобы пользователь сразу был залогинен??
        }
    }

    @Override
    public ResponseEntity<ResponseApi> editProfile(EditProfileRequest editProfileRequest, HttpSession session) {
        File photo = editProfileRequest.getPhoto();
        Byte removePhoto = editProfileRequest.getRemovePhoto();
        String name = editProfileRequest.getName();
        String email = editProfileRequest.getEmail();
        String password = editProfileRequest.getPassword();
        Integer userId = getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        ArrayList<User> allUsers = getAllUsersList();
        boolean hasSameName = false;
        boolean hasSameEmail = false;
        for (User u : allUsers) {
            if (u.getEmail().toLowerCase().equals(email.toLowerCase())) {
                hasSameEmail = true;
            }
            if (u.getName().toUpperCase().equals(name.toUpperCase())) {
                hasSameName = true;
            }
        }
        if (hasSameName || hasSameEmail) { // Такой пользователь уже есть в базе //TODO проверку размера фото, введенного когда капчи
            return new ResponseEntity<>(new ResponseFailEditProfile(), HttpStatus.OK);
        }
        if (photo != null) { // фото есть, тогда устанавливаем путь к загруженному фото пользователю
            // TODO как загрузить фото на сервер и получить путь? Заменить ниже
            String photoUrl = "dsds";
            user.setPhoto(photoUrl);
        }
        if (!name.isBlank()) {
            user.setName(name);
        }
        if (!email.isBlank()) { // TODO проверка соответствия EMAIL виду mail@mail.ru
            user.setEmail(email);
        }
        if (!password.isBlank()) { // Проверить длину и т.п. пароля
            String newHashedPassword = Integer.toString(password.hashCode());
            user.setHashedPassword(newHashedPassword);
        }
        if (removePhoto == 1) {
            //TODO удалить фото с сервера
            user.setPhoto(null);
        }
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getMyStatistics(HttpSession session) {
        Integer userId = getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        LocalDateTime firstPostTime = null;
        Set<Post> myPosts = user.getPosts();
        int postsCount = myPosts.size();
        int allLikesCount = 0;
        int allDislikeCount = 0;
        int viewsCount = 0;
        for (Post p : myPosts) {
            LocalDateTime currentPostTime = p.getTime().toLocalDateTime();
            if (firstPostTime == null) {
                firstPostTime = currentPostTime;
            } else if (firstPostTime.isAfter(currentPostTime)) {
                firstPostTime = currentPostTime;
            }
            viewsCount += p.getViewCount();
            Set<PostVote> currentPostVotes = p.getPostVotes();
            for (PostVote like : currentPostVotes) {
                if (like.getValue() == 1) {
                    allLikesCount += 1;
                } else if (like.getValue() == -1) {
                    allDislikeCount += 1;
                }
            }
        }
        String firstPublicationDate;
        firstPublicationDate = firstPostTime == null ? "Еще не было"
                : firstPostTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        ResponseStatistics responseStatistics = new ResponseStatistics(postsCount, allLikesCount, allDislikeCount,
                viewsCount, firstPublicationDate);
        return new ResponseEntity<>(responseStatistics.getMap(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllStatistics(HttpSession session) {
        HashSet<GlobalSettings> settingsSet = globalSettingsRepositoryService.getAllGlobalSettingsSet();
        boolean isStatisticsIsPublic = false;
        for (GlobalSettings s : settingsSet) {
            if (s.getName().toUpperCase().equals("STATISTICS_IS_PUBLIC")) {
                if (s.getValue().toUpperCase().equals("YES")) {
                    isStatisticsIsPublic = true;
                } else if (s.getValue().toUpperCase().equals("NO")) {
                    isStatisticsIsPublic = false;
                }
            }
        }
        Integer userId = getUserIdBySession(session);
        if (!isStatisticsIsPublic && userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        ArrayList<Post> allPosts = postRepositoryService.getAllPosts();
        int postsCount = allPosts.size();
        int allLikesCount = 0;
        int allDislikeCount = 0;
        int viewsCount = 0;
        LocalDateTime firstPostTime = null;
        for (Post p : allPosts) {
            LocalDateTime currentPostTime = p.getTime().toLocalDateTime();
            if (firstPostTime == null) {
                firstPostTime = currentPostTime;
            } else if (firstPostTime.isAfter(currentPostTime)) {
                firstPostTime = currentPostTime;
            }
            viewsCount += p.getViewCount();
        }
        HashSet<PostVote> postVotes = postVoteRepositoryService.getAllPostVotes();
        for (PostVote like : postVotes) {
            if (like.getValue() == 1) {
                allLikesCount += 1;
            } else if (like.getValue() == -1) {
                allDislikeCount += 1;
            }
        }
        String firstPublicationDate = firstPostTime == null ? "Еще не было" :
                firstPostTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        ResponseStatistics responseStatistics = new ResponseStatistics(postsCount, allLikesCount, allDislikeCount,
                viewsCount, firstPublicationDate);
        return new ResponseEntity<>(responseStatistics.getMap(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseApi> logout(HttpSession session) {
        String sessionId = session.getId();
        if (!sessionIdToUserId.containsKey(sessionId)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseBoolean(true));
        } // По условию всегда возвращает true
        else {
            sessionIdToUserId.remove(sessionId);
            session.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseBoolean(true));
        }
    }

    @Override
    public ArrayList<User> getAllUsersList() {
        ArrayList<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public Integer getUserIdBySession(HttpSession session) {
        return sessionIdToUserId.get(session.getId().toString());
    }

    private String generateRandomString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < KEY_SIZE; i++) {
            builder.append(SYMBOLS_FOR_GENERATOR[(int) (Math.random() * (SYMBOLS_FOR_GENERATOR.length - 1))]);
        }
        return builder.toString();
    }
}
