package main.services.Impl;

import main.model.DTOs.ResultLogoutDTO;
import main.model.DTOs.ResultSignUpDTO;
import main.model.entities.*;
import main.model.repositories.UserRepository;
import main.services.interfaces.*;
import org.json.JSONObject;
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

    @Autowired
    private UserRepository userRepository;
    private Map<String, Integer> sessionIdToUserId = new HashMap<>(); // Храним сессию и ID пользователя

    @Override
    public ResponseEntity<User> getUser(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.isPresent() ?
                new ResponseEntity<>(optionalUser.get(), HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @Override
    public ResponseEntity<?> login(String email, String password, PostRepositoryService postRepositoryService, HttpSession session) {
        ArrayList<User> userList = getAllUsersList();
        String resultString = "result";
        for (User user : userList) {
            if (user.getEmail().equals(email)) {
                if (user.getHashedPassword().equals(Integer.toString(password.hashCode()))) { // пароль совпал по хэшу
                    // TODO сделать проверку наличия сессии в базе
                    sessionIdToUserId.put(session.getId().toString(), user.getId()); // Запоминаем пользователя и сессию
                    JSONObject json = new JSONObject();
                    json.put(resultString, true);
                    JSONObject userJson = new JSONObject();
                    json.put("user", userJson);
                    int moderationCount = postRepositoryService.getModerationCount(user.getId()); // Получаем кол-во постов, которые прошли модерацию по id
                    boolean hasSettings = false;
                    if (user.isModerator()) {
                        hasSettings = true;
                    }
                    userJson.put("id", user.getId()).put("name", user.getName()).put("photo", user.getPhoto()) //TODO если фото нет, так и оставить (null - пусто)?
                            .put("email", user.getEmail()).put("moderation", user.isModerator())
                            .put("moderationCount", moderationCount).put("settings", hasSettings);
                    return new ResponseEntity<Object>(json.toString(), HttpStatus.OK);
                } else {
                    JSONObject json = new JSONObject();
                    json.put(resultString, false);
                    return new ResponseEntity<Object>(json.toString(), HttpStatus.BAD_REQUEST);
                }
            }
        }
        JSONObject json = new JSONObject();
        json.put(resultString, false);
        return new ResponseEntity<Object>(json.toString(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> checkAuth(HttpSession session, PostRepositoryService postRepositoryService) {
        if (!sessionIdToUserId.containsKey(session.getId().toString())) {
            String jsonResult = "{\"result\":true}"; //TODO переделать на JSONObject?
            return new ResponseEntity<>(jsonResult, HttpStatus.UNAUTHORIZED);
        } else {
            int userId = sessionIdToUserId.get(session.toString());
            User user = getUser(userId).getBody();
            if (user != null) {
                JSONObject json = new JSONObject();
                json.put("result", true);
                JSONObject userJson = new JSONObject();
                json.put("user", userJson);
                int moderationCount = postRepositoryService.getModerationCount(user.getId()); // Получаем кол-во постов, которые прошли модерацию по id
                boolean hasSettings = false;
                if (user.isModerator()) {
                    hasSettings = true;
                }
                userJson.put("id", user.getId())
                        .put("name", user.getName())
                        .put("photo", user.getPhoto()) //TODO если фото нет, так и оставить (null - пусто)?
                        .put("email", user.getEmail())
                        .put("moderation", user.isModerator())
                        .put("moderationCount", moderationCount)
                        .put("settings", hasSettings);
                return new ResponseEntity<>(json.toString(), HttpStatus.OK);
            } else {
                String badResult = "Ошибка! Пользователь найден в сессиях, однако отсутствует в БД!";
                System.out.println(badResult);
                return new ResponseEntity<>(badResult, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @Override
    public ResponseEntity<String> restorePassword(String email) {
        JSONObject json = new JSONObject();
        ArrayList<User> userList = getAllUsersList();
        for (User user : userList) {
            if (user.getEmail().equals(email)) { // пользователь найден в базе
                // TODO генерация кода случайным образом не HASHcode, заменить ниже!
                String hash = Integer.toString(Double.toString(
                        Math.pow((Math.random() * Math.random()), 100 * (Math.random()))).hashCode());
                user.setCode(hash); // запоминаем код в базе
                String link = "/login/change-password/" + hash;
                // TODO отправка ссылки на email пользователя!!
                json.put("result", true);
                return new ResponseEntity<>(json.toString(), HttpStatus.OK);
            }
        }
        json.put("result", false);
        return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> changePassword(String code, String password, String captcha, String captchaSecret, CaptchaRepositoryService captchaRepositoryService) {
        JSONObject json = new JSONObject(); // TODO реализовать проверку длины пароля и наличия букв/цифр/символов в пароле во всех методах
        if (code == null || password == null || captcha == null || captchaSecret == null) {
            return new ResponseEntity<>("Введены не все данные", HttpStatus.BAD_REQUEST);
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
                            json.put("result", true);
                            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
                        }
                    }
                }
            }
        }
        json.put("result", false);
        JSONObject errorsJson = new JSONObject();
        json.put("errors", errorsJson);
        errorsJson.put("code", "Ссылка для восстановления пароля устарела.\n" +
                "<a href=”/auth/restore”>Запросить ссылку снова</a>`")
                .put("password", "Пароль короче 6-ти символов")
                .put("captcha", "Код с картинки введён неверно");
        return new ResponseEntity<>(json.toString(), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> register(String email, String name, String password, String captcha, String captcha_secret) {
        if (email == null || name == null || password == null || captcha == null || captcha_secret == null
                || email.equals("") || name.equals("") || password.equals("") || captcha.equals("") || captcha_secret.equals("")) { // TODO проверки сделать
            ResultSignUpDTO resultSignUpDTO = new ResultSignUpDTO();
            return new ResponseEntity<ResultSignUpDTO>(resultSignUpDTO, HttpStatus.BAD_REQUEST);
        } else {    // если проверки прошли, создаем пользователя и возвращаем положительный результат
            Timestamp registrationDateTime = Timestamp.valueOf(LocalDateTime.now());         // тек дата и время
            String hashedPassword = Integer.toString(password.hashCode());
            User user = new User(false, registrationDateTime, name, email, hashedPassword);
            userRepository.save(user); // И можно получить id
            String jsonResult = "{\"result\":true}"; //TODO переделать на JSONObject?
            return new ResponseEntity<>(jsonResult, HttpStatus.OK);
            //TODO Может быть добавить сессию зарегинного юзера в sessionToUserId, чтобы пользователь сразу был залогинен??
        }
    }

    @Override
    public ResponseEntity<String> editProfile(File photo, Byte removePhoto, String name, String email, String password, HttpSession session) {
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
        if (hasSameName || hasSameEmail) { // Такой пользователь уже есть в базе
            JSONObject result = new JSONObject();
            JSONObject errors = new JSONObject();
            errors.put("email", "Этот e-mail уже зарегистрирован")
                    .put("photo", "Фото слишком большое, нужно не более 5 Мб")
                    .put("name", "Имя указано неверно")
                    .put("password", "Пароль короче 6-ти символов")
                    .put("captcha", "Код с картинки введён неверно");
            result.put("result", false).put("errors", errors);
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        }
        if (photo != null) {
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
        return new ResponseEntity<>("{\"result\": true}", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> getMyStatistics(HttpSession session, PostVoteRepositoryService postVoteRepositoryService,
                                                  PostRepositoryService postRepositoryService) {
        Integer userId = getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        LocalDateTime firstPostTime = null;
        ArrayList<Post> allPosts = postRepositoryService.getAllPosts();
        int postsCount = 0;
        int allLikesCount = 0;
        int allDislikeCount = 0;
        int viewsCount = 0;
        for (Post p : allPosts) {
            if (p.getUser().getId() == userId) {
                LocalDateTime currentPostTime = p.getTime().toLocalDateTime();
                if (firstPostTime == null) {
                    firstPostTime = currentPostTime;
                } else if (firstPostTime.isAfter(currentPostTime)) {
                    firstPostTime = currentPostTime;
                }
                viewsCount += p.getViewCount();
                postsCount += 1;
                int currentPostId = p.getId();
                //TODO или добавить поля хранения лайков/дизлайков для каждого поста и т.п.
                HashSet<PostVote> postVotes = postVoteRepositoryService.getAllPostVotes();
                for (PostVote like : postVotes) {
                    if (like.getPost().getId() == currentPostId) {
                        if (like.getValue() == 1) {
                            allLikesCount += 1;
                        } else if (like.getValue() == -1) {
                            allDislikeCount += 1;
                        }
                    }
                }
            }
        }
        String firstPublicationDate;
        if (firstPostTime == null) { // Постов не было
            firstPublicationDate = "Еще не было";
        } else {
            firstPublicationDate = firstPostTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        JSONObject result = new JSONObject();
        result.put("Постов", postsCount).put("Лайков", allLikesCount).put("Дизлайков", allDislikeCount)
                .put("Просмотров", viewsCount).put("Первая публикация", firstPublicationDate);
        return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity getAllStatistics(HttpSession session, //TODO вынести в отдельные методы дублированные блоки кода (проверка пользователя, поиск статистики)
                                           GlobalSettingsRepositoryService globalSettingsRepositoryService,
                                           PostVoteRepositoryService postVoteRepositoryService,
                                           PostRepositoryService postRepositoryService) {
        //TODO получение данных GlobalSettings
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
            //TODO или добавить поля хранения лайков/дизлайков для каждого поста и т.п.
            HashSet<PostVote> postVotes = postVoteRepositoryService.getAllPostVotes();
            for (PostVote like : postVotes) {
                if (like.getValue() == 1) {
                    allLikesCount += 1;
                } else if (like.getValue() == -1) {
                    allDislikeCount += 1;
                }
            }
        }
        String firstPublicationDate;
        if (firstPostTime == null) { // Постов не было
            firstPublicationDate = "Еще не было";
        } else {
            firstPublicationDate = firstPostTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        JSONObject result = new JSONObject();
        result.put("Постов", postsCount).put("Лайков", allLikesCount).put("Дизлайков", allDislikeCount)
                .put("Просмотров", viewsCount).put("Первая публикация", firstPublicationDate);
        return new ResponseEntity<String>(result.toString(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResultLogoutDTO> logout(HttpSession session) {
        String sessionId = session.getId();
        if (!sessionIdToUserId.containsKey(sessionId)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResultLogoutDTO(true));
        } // По условию всегда возвращает true
        else {
            sessionIdToUserId.remove(sessionId);
            session.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body(new ResultLogoutDTO(true));
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
}
