package main.services.Impl;

import main.api.request.*;
import main.api.response.*;
import main.model.entities.*;
import main.model.repositories.CaptchaRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {

    private static final char[] SYMBOLS_FOR_GENERATOR = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    @Value("${user.password.restore_key_length}")
    private int keySize;
    @Value("${user.image.root_folder}")
    private String rootPathToUploadAvatars;
    @Value("${user.password.validation_regex}")
    private String passwordValidationRegex;
    @Value("${user.email.validation_regex}")
    private String emailValidationRegex;
    @Value("${user.image.upload_folder}")
    private String uploadFolder;
    @Value("${user.image.avatars_folder}")
    private String avatarsFolder;
    @Value("${user.image.format}")
    private String imageFormat;
    @Value("${user.image.max_size}")
    private int maxPhotoSizeInBytes;
    @Value("${user.image.upload_timeout_ms}")
    private int timeoutToUploadPhotoMS;
    @Value("${user.timeout_edit_profile}")
    private int timeoutToFinishEditProfileMS;
    @Value("${user.password.restore_pass_message_string}")
    private String restorePassMessageString;
    @Value("${user.password.restore_message_subject}")
    private String restoreMessageSubject;

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
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private CaptchaRepository captchaRepository;
    @Autowired
    private PostRepository postRepository;

    private Map<String, Integer> sessionIdToUserId = new HashMap<>(); // Храним сессию и ID пользователя, по заданию не в БД
    private String tempUploadLink = null;
    private boolean isEditFinished = true;

    @Override
    public ResponseEntity<User> getUser(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.isPresent() ?
                new ResponseEntity<>(optionalUser.get(), HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @Override
    //TODO ? добавить print "В случае неудачной авторизации (на frontend должно отображаться сообщение: “Логин и/или пароль введен(ы) неверно”" ?
    public ResponseEntity<ResponseApi> login(LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        }
        if (user.getHashedPassword().equals(Integer.toString(password.hashCode()))) { // пароль совпал по хэшу
            sessionIdToUserId.put(session.getId().toString(), user.getId()); // Запоминаем пользователя и сессию
            return new ResponseEntity<>(new ResponseLogin(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        }
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
        if (!isEmailValid(email)) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        }
        String hash = generateRandomString();
        user.setCode(hash); // запоминаем код в базе
        userRepository.save(user);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(restoreMessageSubject);
        message.setText(restorePassMessageString + hash);
        this.emailSender.send(message);
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        //TODO у юзера удалять код восстановления
    }

    @Override
    public ResponseEntity<ResponseApi> changePassword(ChangePasswordRequest changePasswordRequest) {
        String code = changePasswordRequest.getCode();
        String password = changePasswordRequest.getPassword();
        String captcha = changePasswordRequest.getCaptcha();
        String captchaSecret = changePasswordRequest.getCaptchaSecret();
        if (code == null || password == null || captcha == null || captchaSecret == null ||
                code.isBlank() || password.isBlank() || captcha.isBlank() || captchaSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        User user = userRepository.getUserByCode(code);
        boolean isCodeValid = (user != null);
        boolean isPassValid = isPasswordValid(password);
        boolean isCaptchaCodeValid = isCaptchaValid(captcha, captchaSecret);
        if (!isCodeValid || !isPassValid || !isCaptchaCodeValid) {
            return new ResponseEntity<>(new ResponseFailChangePass(isCodeValid, isPassValid, isCaptchaCodeValid), HttpStatus.BAD_REQUEST);
        }
        String newHashedPassword = Integer.toString(password.hashCode());
        user.setHashedPassword(newHashedPassword);
        userRepository.save(user);
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseApi> register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String name = email.replaceAll("@.+", "");
        String password = registerRequest.getPassword();
        String captcha = registerRequest.getCaptcha();
        String captchaSecret = registerRequest.getCaptchaSecret();
        if (name.isBlank() || password == null || captcha == null || captchaSecret == null
                || password.isBlank() || captcha.isBlank() || captchaSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        // Проверка уникальности имени и email
        boolean isNameValid = (userRepository.getUserByName(name) == null);
        boolean isEmailValid = (userRepository.getUserByEmail(email.toLowerCase()) == null && isEmailValid(email));
        boolean isCaptchaCodeValid = isCaptchaValid(captcha, captchaSecret);
        boolean isPassValid = isPasswordValid(password);
        if (!isNameValid || !isPassValid || !isCaptchaCodeValid || !isEmailValid) {
            return new ResponseEntity<>(new ResponseFailSignUp(isNameValid, isPassValid,
                    isCaptchaCodeValid, isEmailValid), HttpStatus.BAD_REQUEST);
        }
        // Все проверки прошли, регистрация
        Timestamp registrationDateTime = Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        String hashedPassword = Integer.toString(password.hashCode());
        User user = new User(false, registrationDateTime, name, email, hashedPassword);
        userRepository.save(user); // И можно получить id
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);  //TODO Может быть добавить сессию зарегинного юзера в sessionToUserId, чтобы пользователь сразу был залогинен?? А также удалять капчу?
    }


    @Override
    public ResponseEntity<ResponseApi> editProfile(EditProfileRequest editProfileRequest, HttpSession session) {
//        File photo = editProfileRequest.getPhoto(); // TODO каким образом файл приходит??
        isEditFinished = false;
        Byte removePhoto = editProfileRequest.getRemovePhoto();
        String email = editProfileRequest.getEmail();
        String name = editProfileRequest.getName();
        String password = editProfileRequest.getPassword();
        String captcha = editProfileRequest.getCaptcha();
        String captchaSecret = editProfileRequest.getCaptchaSecret();
        // проверка авторизации
        Integer userId = getUserIdBySession(session);
        if (userId == null) {
            isEditFinished = true;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = getUser(userId).getBody();
        if (user == null) {
            isEditFinished = true;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } // Ошибка, пользователь не найден, а сессия есть
        boolean isNameValid = true;
        boolean isEmailValid = true;
        boolean isCaptchaCodeValid = isCaptchaValid(captcha, captchaSecret);
        boolean isPassValid = true;
        boolean isPhotoValid = true;
        if (password != null && !password.isBlank()) {
            isPassValid = isPasswordValid(password);
            String hashedPassword = Integer.toString(password.hashCode());
            if (isPassValid) user.setHashedPassword(hashedPassword);
        }
        if (name != null && !name.isBlank()) {
            isNameValid = (userRepository.getUserByName(name) == null);
            if (isNameValid) user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            isEmailValid = (userRepository.getUserByEmail(email.toLowerCase()) == null && isEmailValid(email));
            if (isEmailValid) user.setEmail(email);
        }
        if (removePhoto != null && removePhoto == 1) { // Удаляем фото юзера
            String currentPhoto = user.getPhoto();
            if (currentPhoto != null) {
                try {
                    Files.deleteIfExists(Path.of(currentPhoto));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                user.setPhoto(null);
            }
        }
        // Загрузка нового фото
        int count = 0;  // TODO ПЕРЕДЕЛАТЬ! Статическая переменная будет работать как надо только для одного потока (юзера)!
        while (tempUploadLink == null && count++ < timeoutToUploadPhotoMS) { // ждем появления ссылки на фото
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            if (tempUploadLink != null) {
                if (Files.size(Path.of(tempUploadLink)) < maxPhotoSizeInBytes) {
                    user.setPhoto(tempUploadLink);
                } else {
                    isPhotoValid = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isNameValid || !isPassValid || !isCaptchaCodeValid || !isEmailValid || !isPhotoValid) {
            isEditFinished = true;
            return new ResponseEntity<>(new ResponseFailEditProfile(isEmailValid, isPhotoValid, isNameValid,
                    isPassValid, isCaptchaCodeValid), HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        isEditFinished = true;
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
            if (s.getName().toUpperCase().equals(GlobalSettingsRepositoryService.STATISTICS_IS_PUBLIC)) {
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
        int postsCount = postRepository.countAllPosts();
        int allLikesCount = postRepository.countAllLikes();
        int allDislikeCount = postRepository.countAllDislikes();
        int viewsCount = postRepository.countAllViews();
        String firstPublicationDate = postsCount < 1 ? "Еще не было публикаций" : postRepository
                .getFirstPublicationDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    public ResponseEntity<?> uploadImage(MultipartFile image, HttpSession session) throws IOException {
        if (image == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (image.getSize() > maxPhotoSizeInBytes) {
            return new ResponseEntity<>(new ResponseFailEditProfile(true, false,
                    true, true, true), HttpStatus.OK);
        }
        Integer userId = getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!Files.exists(Path.of(rootPathToUploadAvatars))) {
            try {
                Files.createDirectory(Path.of(rootPathToUploadAvatars));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileDestPath;
        File newFile = null;
        do {
            fileDestPath = createDirectoriesAndGetFullPath();
            if (!Files.exists(Path.of(fileDestPath))) {
                Files.createFile(Path.of(fileDestPath));
                newFile = new File(fileDestPath);
            }
        } while (newFile == null);
        copyFile(image, newFile);
        // изображение загружено, готовим ссылку для профиля пользователя
        tempUploadLink = fileDestPath;
        // ждем когда пользователь нажмет кнопку редактировать профиль, если не нажимает, то удаляем фото и обнуляем ссылку
        int count = 0;
        while (!isEditFinished && count < timeoutToFinishEditProfileMS) {
            try {
                Thread.sleep(1);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (isEditFinished) {
            tempUploadLink = null;
            isEditFinished = false;
            return new ResponseEntity<>(fileDestPath, HttpStatus.OK);
        } else {
            Files.deleteIfExists(Paths.get(fileDestPath));
            tempUploadLink = null;
            isEditFinished = false;
        }
        return new ResponseEntity<>(fileDestPath, HttpStatus.OK);
    }

    @Override
    public Integer getUserIdBySession(HttpSession session) {
        return sessionIdToUserId.get(session.getId().toString());
    }

    private String generateRandomString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keySize; i++) {
            builder.append(SYMBOLS_FOR_GENERATOR[(int) (Math.random() * (SYMBOLS_FOR_GENERATOR.length - 1))]);
        }
        return builder.toString();
    }

    private boolean isPasswordValid(String passwordToCheck) {
        return (passwordToCheck != null && passwordToCheck.matches(passwordValidationRegex));
    }

    private boolean isEmailValid(String emailToCheck) {
        return (emailToCheck != null && emailToCheck.matches(emailValidationRegex));
    }

    private void copyFile(MultipartFile source, File dest) {
        try {
            byte[] bytes = source.getBytes();
            Files.write(dest.toPath(), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
//            (File source, File dest) {
//        try {
//            byte[] bytes = Files.readAllBytes(source.toPath());
//            Files.write(dest.toPath(), bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private String createDirectoriesAndGetFullPath() throws IOException {
        String randomHash = String.valueOf(String.valueOf(Math.pow(Math.random(), 100 * Math.random())).hashCode());
        StringBuilder builder = new StringBuilder(rootPathToUploadAvatars).append("/").append(uploadFolder)
                .append("/").append(avatarsFolder);
        Files.createDirectories(Path.of(builder.toString()));
        return builder.append("/").append(randomHash).append(".").append(imageFormat).toString(); // имя файла задаем хэшем
    }

    private Boolean isCaptchaValid(String captcha, String captchaSecret) {
        CaptchaCode captchaCode = captchaRepository.getCaptchaBySecretCode(captchaSecret);
        return captchaCode != null && captchaCode.getCode().equals(captcha);
    }
}
