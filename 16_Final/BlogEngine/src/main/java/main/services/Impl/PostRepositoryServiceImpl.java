package main.services.Impl;

import lombok.extern.slf4j.Slf4j;
import main.api.request.ModeratePostRequest;
import main.api.request.PostRequest;
import main.api.response.*;
import main.model.ModerationStatus;
import main.model.entities.Post;
import main.model.entities.Tag;
import main.model.entities.TagToPost;
import main.model.entities.User;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class PostRepositoryServiceImpl implements PostRepositoryService {
    public static final String PARSE_TIME_PATTERN1 = "yyyy-MM-dd HH:mm";
    public static final String PARSE_TIME_PATTERN2 = "yyyy-MM-dd'T'HH:mm";
    public static final String PARSE_DATE_PATTERN = "yyyy-MM-dd";
    public static final String REGEX_TO_CLEAN_TEXT_FROM_TAGS = "<.+?>";
    @Value("${post.image.root_folder}")
    private String imagesRootFolder;
    @Value("${post.body.min_length}")
    private int postBodyMinLength;
    @Value("${post.body.max_length}")
    private int postBodyMaxLength;
    @Value("${post.title.min_length}")
    private int postTitleMinLength;
    @Value("${post.title.max_length}")
    private int postTitleMaxLength;
    @Value("${post.image.upload_folder}")
    private String imagesUploadFolder;
    @Value("${post.image.format}")
    private String imagesFormat;
    @Value("${post.default_limit_per_page}")
    private int defaultPostsLimitPerPage;
    @Value("${post.announce.max_length}")
    private int announceLength;
    @Value("${user.password.hashing_algorithm}")
    private String hashingAlgorithm;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagRepositoryService tagRepositoryService;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private TagToPostRepositoryService tagToPostRepositoryService;
    @Autowired
    private FileSystemService fileSystemService;
    @Autowired
    private HtmlParseService htmlParserService;

    public ResponseEntity<ResponseApi> getPostForUnlogginedUser(int id) {
        Post post = getPostById(id);
        if (post == null) {
            log.warn("--- Не удалось найти пост с id:" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!post.isActive() || !post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                || !post.getTime().isBefore(LocalDateTime.now())) {
            log.warn("--- Данные поста не соответствуют настройкам отображения: " + post.toString());
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Данные поста не соответствуют настройкам отображения"), HttpStatus.BAD_REQUEST);
        }
        ResponseApi responseApi = new ResponsePost(post, announceLength);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        log.info("--- Получен пост с id:" + id + ", количество просмотров увеличено на 1");
        ResponseEntity<ResponseApi> response = new ResponseEntity<ResponseApi>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    @Override
    public ResponseEntity<ResponseApi> getPost(int id, HttpSession session) {
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        // User найден. Проверяем является ли запрошенный пост его
        Post post = getPostById(id);
        if (post.getUser() != user)
            return getPostForUnlogginedUser(id); // Пользователь не автор, получает пост на общих основаниях
        // Если автор, то просматривает в любом случае, однако просмотры не увеличиваются
        ResponseApi responseApi = new ResponsePost(post, announceLength);
        ResponseEntity<ResponseApi> response = new ResponseEntity<ResponseApi>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public Post getPostById(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.isPresent() ?
                optionalPost.get() : null;
    }

    @Override
    public ResponseEntity<ResponseApi> getRecentPosts() {
        return getPostsWithParams(0, defaultPostsLimitPerPage, "recent");
    }

    public ResponseEntity<ResponseApi> getPostsWithParams(int offset, int limit, String mode) {
        if (offset < 0 || limit < 1 ||
                (!mode.equals("recent") && !mode.equals("popular") && !mode.equals("best") && !mode.equals("early"))) {
            log.warn("--- Неверный сдвиг, лимит или режим отображения постов: " +
                    "offset:" + offset + "," +
                    "limit:" + limit + "," +
                    "mode:" + mode);
            return new ResponseEntity<>(new ResponseBadReqMsg("Неверный сдвиг, лимит или режим отображения постов"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = new ArrayList<>();
//        int count = postRepository.countAllPostsAtDatabase(); // Выдаем теперь число не всех постов, а только число отфильтрованных постов
        int count = postRepository.countAllPostsAtSite(); // Выдаем теперь число не всех постов, а только число полученных в запросе
        switch (mode.toLowerCase()) {
            case ("recent"):
                postsToShow = postRepository.getRecentPosts(offset, limit);
                break;
            case ("popular"):
                postsToShow = postRepository.getPopularPosts(offset, limit);
                break;
            case ("best"):
                postsToShow = postRepository.getBestPosts(offset, limit);
                break;
            case ("early"):
                postsToShow = postRepository.getEarlyPosts(offset, limit);
                break;
        }
        log.info("--- Для отображения получены посты с параметрами: " +
                "offset:" + offset + "," +
                "limit:" + limit + "," +
                "mode:" + mode);
        ResponseApi responseApi = new ResponsePosts(count, (ArrayList<Post>) postsToShow, announceLength);
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> searchPosts(int offset, String query, int limit) {
        if (offset < 0 || limit < 1) {
            log.warn("--- Неверный сдвиг или лимит постов: " +
                    "offset:" + offset + "," +
                    "limit:" + limit);
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига и/или лимита"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = postRepository.searchPosts(offset, limit, query);
        log.info("--- Для отображения получены посты с параметрами: " +
                "offset:" + offset + "," +
                "limit:" + limit + "," +
                "query:" + query);
        int count = postRepository.countSearchedPosts(query);
        ResponseApi responseApi = new ResponsePosts(count, (ArrayList<Post>) postsToShow, announceLength);
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> getPostsByDate(String dateString, int offset, int limit) {
        LocalDate date = parseLocalDate(dateString);
        if (offset < 0 || limit < 1 || date == null) {
            log.warn("--- Неверные параметры: " +
                    "offset:" + offset + "," +
                    "limit:" + limit + "," +
                    "date:" + date
            );
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или даты"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = postRepository.getPostsByDate(dateString, limit, offset);
        int count = postRepository.countPostsByDate(dateString);
        log.info("--- Для отображения получены посты с параметрами: " +
                "offset:" + offset + "," +
                "limit:" + limit + "," +
                "date:" + dateString);
        ResponseApi responseApi = new ResponsePosts(count, (ArrayList<Post>) postsToShow, announceLength);
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> getPostsByTag(int limit, String tag, int offset) {
        if (offset < 0 || limit < 1 || tag == null || tag.equals("")) {
            log.warn("--- Неверные параметры: " +
                    "offset:" + offset + "," +
                    "limit:" + limit + "," +
                    "tag:" + tag
            );
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или тэга"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = postRepository.getPostsByTag(tag, limit, offset);
        int count = postRepository.countPostsByTag(tag);
        log.info("--- Для отображения получены посты с параметрами: " +
                "offset:" + offset + "," +
                "limit:" + limit + "," +
                "tag:" + tag);
        ResponseApi responseApi = new ResponsePosts(count, (ArrayList<Post>) postsToShow, announceLength);
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> getPostsForModeration(String status, int offset, int limit,
                                                             HttpSession session) {
        if (offset < 0 || limit < 1 ||
                (!status.equalsIgnoreCase("new") && !status.equalsIgnoreCase("declined")
                        && !status.equalsIgnoreCase("accepted"))) {
            log.warn("--- Неверные параметры: " +
                    "offset:" + offset + "," +
                    "limit:" + limit + "," +
                    "status:" + status
            );
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или статуса модерации"), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!user.isModerator()) {
            log.info("--- Для данного действия пользователю " + user.getId() + ":" + user.getName() + " требуются права модератора");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Недостаточно прав
        }
        // Пост при создании не имеет moderator_id, показываем всем модераторам все новые посты
        ArrayList<Post> postsToShow = new ArrayList<>();
        int count = 0;
        switch (status) {
            case ("new"):
                postsToShow = (ArrayList<Post>) postRepository.getPostsForModeration(limit, offset);
                count = postRepository.countPostsForModeration();
                break;
            case ("declined"):
                postsToShow = (ArrayList<Post>) postRepository
                        .getPostsModeratedByMe("DECLINED", limit, offset, user.getId());
                count = postRepository.countPostsModeratedByMe("DECLINED", user.getId());
                break;
            case ("accepted"):
                postsToShow = (ArrayList<Post>) postRepository
                        .getPostsModeratedByMe("ACCEPTED", limit, offset, user.getId());
                count = postRepository.countPostsModeratedByMe("ACCEPTED", user.getId());
                break;
        }
        log.info("--- Для отображения получены посты с параметрами: " +
                "offset:" + offset + "," +
                "limit:" + limit + "," +
                "status:" + status);
        ResponseApi responseApi = new ResponsePostsForModeration(count, postsToShow, announceLength);
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> getMyPosts(String status, int offset, int limit, HttpSession session) {
        if (offset < 0 || limit < 1 || (!status.equals("inactive")
                && !status.equalsIgnoreCase("pending") && !status.equalsIgnoreCase("declined")
                && !status.equalsIgnoreCase("published"))) {
            log.warn("--- Неверные параметры: " +
                    "offset:" + offset + "," +
                    "limit:" + limit + "," +
                    "status:" + status
            );
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или статуса"), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        ArrayList<Post> postsToShow = new ArrayList<>();
        int count = 0;
        switch (status) {
            case ("inactive"):
                postsToShow = (ArrayList<Post>) postRepository.getMyNotActivePosts(user.getId(), limit, offset);
                count = postRepository.countMyNotActivePosts(user.getId());
                break;
            case ("pending"):
                postsToShow = (ArrayList<Post>) postRepository.getMyActivePosts("NEW", limit, offset, user.getId());
                count = postRepository.countMyActivePosts("NEW", user.getId());
                break;
            case ("declined"):
                postsToShow = (ArrayList<Post>) postRepository.getMyActivePosts("DECLINED", limit, offset, user.getId());
                count = postRepository.countMyActivePosts("DECLINED", user.getId());
                break;
            case ("published"):
                postsToShow = (ArrayList<Post>) postRepository.getMyActivePosts("ACCEPTED", limit, offset, user.getId());
                count = postRepository.countMyActivePosts("ACCEPTED", user.getId());
                break;
        }
        log.info("--- Для отображения получены посты с параметрами: " +
                "offset:" + offset + "," +
                "limit:" + limit + "," +
                "status:" + status);
        ResponseApi responseApi = new ResponseMyPosts(count, postsToShow, announceLength);
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(responseApi, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> post(PostRequest postRequest, HttpSession session) {
        String timeString = postRequest.getTime();
        byte active = postRequest.getActive();
        String title = postRequest.getTitle();
        String text = postRequest.getText();
        List<String> postTags = postRequest.getTags();
        LocalDateTime time = parseLocalDateTime(timeString);
//        if (time == null) {
//            return new ResponseEntity<>(new ResponseBadReqMsg("Неверный параметр времени создания поста"), HttpStatus.BAD_REQUEST);
//        }
        time = time == null || time.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : time;
        boolean isTextValidFlag = isTextValid(text);
        boolean isTitleValidFlag = isTitleValid(title);
        if (!isTextValidFlag || !isTitleValidFlag) {
            log.warn("--- Неверные параметры: " +
                    "text:" + text + "," +
                    "title:" + title
            );
            return new ResponseEntity<>(new ResponseFailPost(isTextValidFlag, isTitleValidFlag), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        boolean isActive = false;
        if (active == 1) {
            isActive = true;
        }
        Post currentPost = postRepository.save(new Post(isActive, ModerationStatus.NEW, user, time, title, text));
        log.info("--- Создан новый пост с id:" + currentPost.getId());
        for (String currentTagName : postTags) {
            if (!currentTagName.isBlank()) {
                Tag currentTag = tagRepository.getTagByTagName(currentTagName) != null ? // Создаем теги, только если их еще не было
                        tagRepository.getTagByTagName(currentTagName) :
                        tagRepositoryService.addTag(new Tag(currentTagName));
                tagToPostRepositoryService.addTagToPost(new TagToPost(currentTag, currentPost)); // Возможно надо добавлять в Set у тегов и у постов
                log.info("--- К созданному посту с id:" + currentPost.getId() + " добавлен тэг:" + currentTagName);
            }
        }
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<?> uploadImage(MultipartFile image, HttpSession session) throws IOException {
        if (image == null) {
            log.warn("--- Отсутствует изображение для загрузки");
            return new ResponseEntity<>(new ResponseBadReqMsg("Файл для загрузки отсутствует"), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } // Ошибка, пользователь не найден, а сессия есть
        if (!Files.exists(Path.of(imagesRootFolder))) {
            fileSystemService.createDirectoriesByPath(imagesRootFolder);
//            try {
//                Files.createDirectory(Path.of(imagesRootFolder));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        String fileDestPath;
        String directoryPath = getRandomDirectoryToUpload();
        if (fileSystemService.createDirectoriesByPath(directoryPath)) {
            String imageName = getRandomImageName();
            fileDestPath = directoryPath + "/" + imageName;
            while (Files.exists(Path.of(fileDestPath))) {
                imageName = getRandomImageName();
                fileDestPath = directoryPath + "/" + imageName;
            }
            fileSystemService.copyMultiPartFileToPath(image, Paths.get(directoryPath, imageName));
        } else {
            log.warn("--- Не удалось создать папку: " + directoryPath);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Не удалось создать папку
        }
        ResponseEntity<String> response = new ResponseEntity<>(fileDestPath, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> editPost(int id, PostRequest postRequest, HttpSession session) {
        String timeString = postRequest.getTime();
        byte active = postRequest.getActive();
        String title = postRequest.getTitle();
        String text = postRequest.getText();
        List<String> tags = postRequest.getTags();
        LocalDateTime time = parseLocalDateTime(timeString);
        Post currentPost = getPostById(id);
        if (currentPost == null) {
            log.warn("--- Не найдет пост с id:" + id);
            return new ResponseEntity<>(new ResponseBadReqMsg("Пост не найден"), HttpStatus.BAD_REQUEST);
        }// Пост не существует
        if (time == null || time.isBefore(LocalDateTime.now())) time = LocalDateTime.now();
        boolean isTextValidFlag = isTextValid(text);
        boolean isTitleValidFlag = isTitleValid(title);
        if (!isTextValidFlag || !isTitleValidFlag) {
            log.warn("--- Неверные параметры: " +
                    "text:" + text + "," +
                    "title:" + title
            );
            return new ResponseEntity<>(new ResponseFailPost(isTextValidFlag, isTitleValidFlag), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } // Ошибка, пользователь не найден, а сессия есть
        currentPost.setActive(active == 1);
        currentPost.setTime(time);
        currentPost.setTitle(title);
        currentPost.setText(text);
        currentPost.setModerationStatus(ModerationStatus.NEW);
        postRepository.save(currentPost); // Пост пересохранили, меняем теги и связи с тегами, удаляя старые и добавляя новые
        currentPost.getTagsToPostsSet().forEach(tag2post -> {
            tagRepositoryService.deleteTag(tag2post.getTag());
            tagToPostRepositoryService.deleteTagToPost(tag2post);
        });
        log.info("--- Удалены теги к посту с id:" + currentPost.getId());
        for (String currentTagName : tags) {
            if (!currentTagName.isBlank()) {
                Tag currentTag = tagRepositoryService.addTag(new Tag(currentTagName));
                tagToPostRepositoryService.addTagToPost(new TagToPost(currentTag, currentPost));
                log.info("--- К посту с id:" + currentPost.getId() + " добавлен тэг {" + currentTagName + "}");
            }
        }
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> moderatePost(ModeratePostRequest moderatePostRequest, HttpSession session) {
        int postId = moderatePostRequest.getPostId();
        String decision = moderatePostRequest.getDecision();
        decision = decision.toUpperCase().trim();
        if (!decision.equals("DECLINE") && !decision.equals("ACCEPT")) {
            log.warn("--- Неверный параметр: decision:" + decision);
            return new ResponseEntity<>(new ResponseBadReqMsg("Решение по модерации не распознано"), HttpStatus.BAD_REQUEST);
        }
        // Если пользователь залогинен
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } // Пользователь не найден, хотя сессия есть
        if (!user.isModerator()) {
            log.info("--- Для данного действия пользователю " + user.getId() + ":" + user.getName() + " требуются права модератора");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Post post = getPostById(postId);
        if (post == null) {
            log.warn("--- Не найден пост с id: " + postId);
            return new ResponseEntity<>(new ResponseBadReqMsg("Пост не найден"), HttpStatus.BAD_REQUEST);
        }
        post.setModeratorId(userId);
        if (decision.equals("DECLINE")) {
            post.setModerationStatus(ModerationStatus.DECLINED);
        } else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
        postRepository.save(post);
        ResponseEntity<ResponseApi> response = ResponseEntity.status(HttpStatus.OK).body(null);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<ResponseApi> countPostsByYear(Integer queriedYear) {
        int year = queriedYear == null ? LocalDateTime.now().getYear() : queriedYear;
        List<Post> postsByYear = postRepository.getPostsByYear(year);
        log.info("--- Получен список постов за " + year + " год");
        HashMap<Date, Integer> postsCountByDate = new HashMap<>();
        for (Post p : postsByYear) {
            Date postDate = Date.valueOf(p.getTime().toLocalDate());
            Integer postCount = postsCountByDate.getOrDefault(postDate, 0);
            postsCountByDate.put(postDate, postCount + 1);
        }
        List<Integer> allYears = postRepository.getYearsWithAnyPosts();
        log.info("--- Получен список всех лет, за которые есть посты: " + Arrays.toString(allYears.toArray()));
        ResponseEntity<ResponseApi> response =
                new ResponseEntity<ResponseApi>(new ResponsePostsCalendar(postsCountByDate, allYears), HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    @Override
    public ArrayList<Post> getAllPosts() {
        return new ArrayList<>(postRepository.findAll());
    }

    private String getRandomDirectoryToUpload() {
        String randomHash = String.valueOf(String.valueOf(Math.pow(Math.random(), 100 * Math.random())).hashCode());
        String firstFolder = randomHash.substring(0, randomHash.length() / 3);
        String secondFolder = randomHash.substring(
                firstFolder.length(), (firstFolder.length() + randomHash.length() / 3));
        String thirdFolder = randomHash.substring((firstFolder.length() + secondFolder.length()));
        StringBuilder builder = new StringBuilder(imagesRootFolder)
                .append("/").append(imagesUploadFolder)
                .append("/").append(firstFolder)
                .append("/").append(secondFolder)
                .append("/").append(thirdFolder);
        String result = builder.toString();
        log.info("--- Получена случайная папка для загрузки: " + result);
        return result;
    }

    private String getRandomImageName() {
        String randomHash = getHashedString(String.valueOf(Math.pow(Math.random(), 100 * Math.random())));
        String res = randomHash + "." + imagesFormat; // имя файла задаем хэшем
        log.info("--- Получено имя файла: " + res);
        return res;
    }

    private boolean isTextValid(String text) {
        if (text == null || text.equals("")) return false;
        String cleanText = htmlParserService.getTextStringFromHtml(text);
        return cleanText.length() <= postBodyMaxLength && cleanText.length() >= postBodyMinLength;
    }

    private boolean isTitleValid(String title) {
        return title != null && !title.equals("") && title.length() <= postTitleMaxLength
                && title.length() >= postTitleMinLength;
    }

    private String getHashedString(String stringToHash) {
        log.info("--- Получаем хэш-строку по алгоритму: " + hashingAlgorithm + " из строки {" + stringToHash + "}");
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            md.update(stringToHash.getBytes());
            byte[] digest = md.digest();
            String result = DatatypeConverter.printHexBinary(digest).toUpperCase();
            log.info("--- Успешно получена хэш-строка по алгоритму: " + hashingAlgorithm + " из строки {" + stringToHash + "}");
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.info("--- Не удалось получить хэш-строку по алгоритму: " + hashingAlgorithm + " из строки {" + stringToHash + "}");
            return null;
        }
    }

    private LocalDateTime parseLocalDateTime(String timeToParse) {
        log.info("--- Парсинг времени из строки {" + timeToParse + "}");
        LocalDateTime time = null;
        try {
            time = LocalDateTime.ofInstant(new SimpleDateFormat(PARSE_TIME_PATTERN1)
                    .parse(timeToParse).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            log.error("--- Не удалось спарсить время из строки {" + timeToParse +
                    "} с паттерном {" + PARSE_TIME_PATTERN1 + "}", e);
            try {
                time = LocalDateTime.ofInstant(new SimpleDateFormat(PARSE_TIME_PATTERN2)
                        .parse(timeToParse).toInstant(), ZoneId.systemDefault());
            } catch (ParseException ex) {
                log.error("--- Не удалось спарсить время из строки {" + timeToParse +
                        "} с паттерном {" + PARSE_TIME_PATTERN2 + "}", ex);
                ex.printStackTrace();
                e.printStackTrace();
            }
        }
        log.info("--- Получено время {" + time + "} из строки {" + timeToParse + "}");
        return time;
    }

    private LocalDate parseLocalDate(String dateToParse) {
        log.info("--- Парсинг даты из строки {" + dateToParse + "}");
        LocalDate date = null;
        try {
            date = LocalDate.ofInstant(new SimpleDateFormat(PARSE_DATE_PATTERN)
                    .parse(dateToParse).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
            log.error("--- Не удалось спарсить дату из строки {" + dateToParse + "}", e);
        }
        log.info("--- Получена дата {" + date + "} из строки {" + dateToParse + "}");
        return date;
    }
}