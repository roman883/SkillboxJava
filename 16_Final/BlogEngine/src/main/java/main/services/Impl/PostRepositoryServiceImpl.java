package main.services.Impl;

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
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PostRepositoryServiceImpl implements PostRepositoryService {
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
    @Transient // TODO или убрать?
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


    public ResponseEntity<ResponseApi> getPost(int id) {
        Post post = getPostById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!post.isActive() || !post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                || !post.getTime().isBefore(LocalDateTime.now())) {  // Не все данные отображаются, проверить время
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Данные поста не соответствуют настройкам отображения"), HttpStatus.BAD_REQUEST);
        }
        ResponseApi responseApi = new ResponsePost(post, announceLength);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return new ResponseEntity<ResponseApi>(responseApi, HttpStatus.OK);
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
            return new ResponseEntity<>(new ResponseBadReqMsg("Неверный сдвиг, лимит или режим отображения постов"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = new ArrayList<>();
        int count = postRepository.countAllPosts();
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
        ResponseApi responseApi = new ResponsePosts(count, (ArrayList<Post>) postsToShow, announceLength);
        return new ResponseEntity<>(responseApi, HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> searchPosts(int offset, String query, int limit) {
        if (offset < 0 || limit < 1) {
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига и/или лимита"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = postRepository.searchPosts(offset, limit, query);
        ResponseApi responseApi = new ResponsePosts(postsToShow.size(), (ArrayList<Post>) postsToShow, announceLength);
        return new ResponseEntity<>(responseApi, HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> getPostsByDate(String dateString, int offset, int limit) {
        LocalDate date = parseLocalDate(dateString);
        if (offset < 0 || limit < 1 || date == null) {
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или даты"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = postRepository.getPostsByDate(dateString, limit, offset);
        ResponseApi responseApi = new ResponsePosts(postsToShow.size(), (ArrayList<Post>) postsToShow, announceLength);
        return new ResponseEntity<>(responseApi, HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> getPostsByTag(int limit, String tag, int offset) {
        if (offset < 0 || limit < 1 || tag == null || tag.equals("")) {
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или тэга"), HttpStatus.BAD_REQUEST);
        }
        List<Post> postsToShow = postRepository.getPostsByTag(tag, limit, offset);
        ResponseApi responseApi = new ResponsePosts(postsToShow.size(), (ArrayList<Post>) postsToShow, announceLength);
        return new ResponseEntity<>(responseApi, HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> getPostsForModeration(String status, int offset, int limit,
                                                             HttpSession session) {
        if (offset < 0 || limit < 1 ||
                (!status.equalsIgnoreCase("new") && !status.equalsIgnoreCase("declined")
                        && !status.equalsIgnoreCase("accepted"))) {
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или статуса модерации"), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!user.isModerator()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Недостаточно прав
        }
        // Пост при создании не имеет moderator_id, показываем всем модераторам все новые посты
        ArrayList<Post> postsToShow = new ArrayList<>();
        switch (status) {
            case ("new"):
                postsToShow = (ArrayList<Post>) postRepository.getPostsForModeration(limit, offset);
                break;
            case ("declined"):
                postsToShow = (ArrayList<Post>) postRepository
                        .getPostsModeratedByMe("DECLINED", limit, offset, user.getId());
                break;
            case ("accepted"):
                postsToShow = (ArrayList<Post>) postRepository
                        .getPostsModeratedByMe("ACCEPTED", limit, offset, user.getId());
                break;
        }
        ResponseApi responseApi = new ResponsePostsForModeration(postsToShow.size(), postsToShow, announceLength);
        return new ResponseEntity<>(responseApi, HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> getMyPosts(String status, int offset, int limit, HttpSession session) {
        if (offset < 0 || limit < 1 || (!status.equals("inactive")
                && !status.equalsIgnoreCase("pending") && !status.equalsIgnoreCase("declined")
                && !status.equalsIgnoreCase("published"))) {
            return new ResponseEntity<>(
                    new ResponseBadReqMsg("Неверные параметры сдвига, лимита или статуса"), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        ArrayList<Post> postsToShow = new ArrayList<>();
        switch (status) {
            case ("inactive"):
                postsToShow = (ArrayList<Post>) postRepository.getMyNotActivePosts(user.getId(), limit, offset);
                break;
            case ("pending"):
                postsToShow = (ArrayList<Post>) postRepository.getMyActivePosts("NEW", limit, offset, user.getId());
                break;
            case ("declined"):
                postsToShow = (ArrayList<Post>) postRepository.getMyActivePosts("DECLINED", limit, offset, user.getId());
                break;
            case ("published"):
                postsToShow = (ArrayList<Post>) postRepository.getMyActivePosts("ACCEPTED", limit, offset, user.getId());
                break;
        }
        ResponseApi responseApi = new ResponseMyPosts(postsToShow.size(), postsToShow, announceLength);
        return new ResponseEntity<>(responseApi, HttpStatus.OK);
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
            return new ResponseEntity<>(new ResponseFailPost(isTextValidFlag, isTitleValidFlag), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        boolean isActive = false;
        if (active == 1) {
            isActive = true;
        }
        Post currentPost = postRepository.save(new Post(isActive, ModerationStatus.NEW, user, time, title, text));
        for (String currentTagName : postTags) {
            if (!currentTagName.isBlank()) {
                Tag currentTag = tagRepository.getTagByTagName(currentTagName) != null ? // Создаем теги, только если их еще не было
                        tagRepository.getTagByTagName(currentTagName) :
                        tagRepositoryService.addTag(new Tag(currentTagName));
                tagToPostRepositoryService.addTagToPost(new TagToPost(currentTag, currentPost)); // Возможно надо добавлять в Set у тегов и у постов
            }
        }
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
    }

    public ResponseEntity<?> uploadImage(MultipartFile image, HttpSession session) throws IOException {
        if (image == null)
            return new ResponseEntity<>(new ResponseBadReqMsg("Файл для загрузки отсутствует"), HttpStatus.BAD_REQUEST);
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        if (!Files.exists(Path.of(imagesRootFolder))) {
            try {
                Files.createDirectory(Path.of(imagesRootFolder));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            image.transferTo(Paths.get(directoryPath, imageName));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Не удалось создать папку
        }
        return new ResponseEntity<>(fileDestPath, HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> editPost(int id, PostRequest postRequest, HttpSession session) {
        String timeString = postRequest.getTime();
        byte active = postRequest.getActive();
        String title = postRequest.getTitle();
        String text = postRequest.getText();
        List<String> tags = postRequest.getTags();
        LocalDateTime time = parseLocalDateTime(timeString);
        Post currentPost = getPostById(id);
        if (currentPost == null)
            return new ResponseEntity<>(new ResponseBadReqMsg("Пост не найден"), HttpStatus.BAD_REQUEST); // Пост не существует
        if (time == null || time.isBefore(LocalDateTime.now())) time = LocalDateTime.now();
        boolean isTextValidFlag = isTextValid(text);
        boolean isTitleValidFlag = isTitleValid(title);
        if (!isTextValidFlag || !isTitleValidFlag) {
            return new ResponseEntity<>(new ResponseFailPost(isTextValidFlag, isTitleValidFlag), HttpStatus.BAD_REQUEST);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
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
        for (String currentTagName : tags) {
            if (!currentTagName.isBlank()) {
                Tag currentTag = tagRepositoryService.addTag(new Tag(currentTagName));
                tagToPostRepositoryService.addTagToPost(new TagToPost(currentTag, currentPost));
            }
        }
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> moderatePost(ModeratePostRequest moderatePostRequest, HttpSession session) {
        int postId = moderatePostRequest.getPostId();
        String decision = moderatePostRequest.getDecision();
        decision = decision.toUpperCase().trim();
        if (!decision.equals("DECLINE") && !decision.equals("ACCEPT")) {
            return new ResponseEntity<>(new ResponseBadReqMsg("Решение по модерации не распознано"), HttpStatus.BAD_REQUEST);
        }
        // Если пользователь залогинен
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Пользователь не найден, хотя сессия есть
        if (!user.isModerator()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        Post post = getPostById(postId);
        if (post == null) {
            return new ResponseEntity<>(new ResponseBadReqMsg("Пост не найден"), HttpStatus.BAD_REQUEST);
        }
        post.setModeratorId(userId);
        if (decision.equals("DECLINE")) {
            post.setModerationStatus(ModerationStatus.DECLINED);
        } else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
        postRepository.save(post);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    public ResponseEntity<ResponseApi> countPostsByYear(Integer year) {
        List<Post> postsByYear = year == null
                ? postRepository.getPostsByYear(LocalDateTime.now().getYear())
                : postRepository.getPostsByYear(year);
        HashMap<Date, Integer> postsCountByDate = new HashMap<>();
        for (Post p : postsByYear) {
            Date postDate = Date.valueOf(p.getTime().toLocalDate());
            Integer postCount = postsCountByDate.getOrDefault(postDate, 0);
            postsCountByDate.put(postDate, postCount + 1);
        }
        List<Integer> allYears = postRepository.countYearsWithAnyPosts();
        return new ResponseEntity<ResponseApi>(new ResponsePostsCalendar(postsCountByDate, allYears), HttpStatus.OK);
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
        return builder.toString();
    }

    private String getRandomImageName() {
        String randomHash = getHashedString(String.valueOf(Math.pow(Math.random(), 100 * Math.random())));
        return randomHash + "." + imagesFormat; // имя файла задаем хэшем
    }

    private boolean isTextValid(String text) {
        return text != null && !text.equals("") && text.length() <= postBodyMaxLength
                && text.length() >= postBodyMinLength;
    }

    private boolean isTitleValid(String title) {
        return title != null && !title.equals("") && title.length() <= postTitleMaxLength
                && title.length() >= postTitleMinLength;
    }

    private String getHashedString(String stringToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingAlgorithm);
            md.update(stringToHash.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LocalDateTime parseLocalDateTime(String timeToParse) {
        LocalDateTime time = null;
        try {
            time = LocalDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .parse(timeToParse).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            try {
                time = LocalDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                        .parse(timeToParse).toInstant(), ZoneId.systemDefault());
            } catch (ParseException ex) {
                ex.printStackTrace();
                e.printStackTrace();
            }
        }
        return time;
    }

    private LocalDate parseLocalDate(String dateToParse) {
        LocalDate date = null;
        try {
            date = LocalDate.ofInstant(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(dateToParse).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}