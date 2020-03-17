package main.services.Impl;

import main.model.ModerationStatus;
import main.model.entities.*;
import main.model.repositories.PostRepository;
import main.model.responses.*;
import main.services.interfaces.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PostRepositoryServiceImpl implements PostRepositoryService {
    private static final String ROOT_PATH_TO_UPLOAD = "images";

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepositoryService tagRepositoryService;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private TagToPostRepositoryService tagToPostRepositoryService;


    public ResponseEntity<ResponseAPI> getPost(int id) {
        Post post = getPostById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!post.isActive() || !post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                || !post.getTime().toLocalDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        ResponseAPI responseAPI = new ResponsePost(post);
        return new ResponseEntity<ResponseAPI>(responseAPI, HttpStatus.OK);
    }

    public Post getPostById(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.isPresent() ?
                optionalPost.get() : null;
    }

    public ResponseEntity<ResponseAPI> getPostsWithParams(int offset, int limit, String mode) { //TODO проверки входных значений?
        List<Post> allPosts = getAllPosts();
        int count = allPosts.size();
        switch (mode.toLowerCase()) { // Сортировка
            case ("recent"):
                Comparator<Post> compareByTimeNewFirst = new Comparator<Post>() {
                    public int compare(Post o1, Post o2) {
                        return o2.getTime().compareTo(o1.getTime());
                    }
                };
                allPosts.sort(compareByTimeNewFirst);
                break;
            case ("popular"):
                Comparator<Post> compareByCommentsCount = (o1, o2) -> o2.getPostComments().size() - o1.getPostComments().size();
                allPosts.sort(compareByCommentsCount);
                break;
            case ("best"):
                Comparator<Post> compareByLikesCount = (o1, o2) -> {
                    int o1likesCount = (int) o1.getPostVotes().stream().filter(like -> like.getValue() == 1).count();
                    int o2LikesCount = (int) o2.getPostVotes().stream().filter(like -> like.getValue() == 1).count();
                    return o2LikesCount - o1likesCount;
                };
                allPosts.sort(compareByLikesCount);
                break;
            case ("early"):
                Comparator<Post> compareByTimeOldFirst = Comparator.comparing(Post::getTime);
                allPosts.sort(compareByTimeOldFirst);
                break;
        }
        ArrayList<Post> postsToShow = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            int index = offset + i;
            if (index == count) {
                break;
            }
            Post currentPost = allPosts.get(index);
            if (currentPost.isActive() && currentPost.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                    && !currentPost.getTime().toLocalDateTime().isAfter(LocalDateTime.now())) {
                postsToShow.add(currentPost);
            }
        }
        ResponseAPI responseAPI = new ResponsePosts(count, postsToShow);
        return new ResponseEntity<>(responseAPI, HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> searchPosts(int offset, String query, int limit) {
        ArrayList<Post> allPosts = getAllPosts();
        int count = allPosts.size();
        ArrayList<Post> postsToShow = new ArrayList<>();
        if (query.equals("")) {
            postsToShow = allPosts;
        } else {
            for (int i = 0; i < limit; i++) {
                int index = offset + i;
                if (index == count) {
                    break;
                }
                Post currentPost = allPosts.get(index);
                if (currentPost.isActive() && currentPost.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                        && !currentPost.getTime().toLocalDateTime().isAfter(LocalDateTime.now())
                        && currentPost.getText().contains(query)) {
                    postsToShow.add(currentPost);
                }
            }
        }
        ResponseAPI responseAPI = new ResponsePosts(postsToShow.size(), postsToShow);
        return new ResponseEntity<>(responseAPI, HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> getPostsByDate(String dateString, int offset, int limit) {
        LocalDate date = null;
        try {
            date = LocalDate.ofInstant(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(dateString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        ArrayList<Post> allPosts = getAllPosts();
        ArrayList<Post> postsToShow = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            int index = offset + i;
            if (index == allPosts.size()) {
                break;
            }
            Post currentPost = allPosts.get(index);
            if (currentPost.isActive() && currentPost.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                    && !currentPost.getTime().toLocalDateTime().isAfter(LocalDateTime.now())
                    && currentPost.getTime().toLocalDateTime().toLocalDate().isEqual(date)) {
                postsToShow.add(currentPost);
            }
        }
        ResponseAPI responseAPI = new ResponsePosts(postsToShow.size(), postsToShow);
        return new ResponseEntity<>(responseAPI, HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> getPostsByTag(int limit, String tag, int offset) {
        if (tag.equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Set<Tag> allTags = tagRepositoryService.getAllTags();
        ArrayList<Post> allPostsByTag = new ArrayList<>();
        allTags.stream().filter(t -> t.getName().contains(tag))
                .forEach(m -> m.getTagsToPostsSet()
                        .forEach(k -> allPostsByTag.add(k.getPost())));
        ArrayList<Post> postsToShow = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            int index = offset + i;
            if (index == allPostsByTag.size()) {
                break;
            }
            Post currentPost = allPostsByTag.get(index);
            if (currentPost.isActive() && currentPost.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                    && !currentPost.getTime().toLocalDateTime().isAfter(LocalDateTime.now())) {
                postsToShow.add(currentPost);
            }
        }
        ResponseAPI responseAPI = new ResponsePosts(postsToShow.size(), postsToShow);
        return new ResponseEntity<>(responseAPI, HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> getPostsForModeration(String status, int offset, int limit,
                                                             HttpSession session) {
        // Авторизован ли пользователь
        if (status.equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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
        ArrayList<Post> queriedPosts = new ArrayList<>();
        switch (status) {
            case ("new"):
                queriedPosts = getAllPosts().stream()
                        .filter(p -> p.getModerationStatus().equals(ModerationStatus.NEW))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case ("declined"):
                queriedPosts = getAllPosts().stream()
                        .filter(u -> u.getModeratorId().equals(userId))
                        .filter(p -> p.getModerationStatus().equals(ModerationStatus.DECLINED))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case ("accepted"):
                queriedPosts = getAllPosts().stream()
                        .filter(u -> u.getModeratorId().equals(userId))
                        .filter(p -> p.getModerationStatus().equals(ModerationStatus.ACCEPTED))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
        }
        ArrayList<Post> postsToShow = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            int index = offset + i;
            if (index == queriedPosts.size()) {
                break;
            }
            Post currentPost = queriedPosts.get(index);
            if (currentPost.isActive()) {
                postsToShow.add(currentPost);
            }
        }
        ResponseAPI responseAPI = new ResponsePostsForModeration(postsToShow.size(), postsToShow);
        return new ResponseEntity<>(responseAPI, HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> getMyPosts(String status, int offset, int limit, HttpSession session) {
        if (status.equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        Set<Post> allMyPosts = user.getPosts();
        ArrayList<Post> queriedPosts = new ArrayList<>();
        switch (status) {
            case ("inactive"):
                queriedPosts = allMyPosts.stream().filter(p -> !p.isActive())
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case ("pending"):
                queriedPosts = allMyPosts.stream()
                        .filter(p -> p.isActive() && p.getModerationStatus().equals(ModerationStatus.NEW))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case ("declined"):
                queriedPosts = allMyPosts.stream()
                        .filter(p -> p.isActive() && p.getModerationStatus().equals(ModerationStatus.DECLINED))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
            case ("published"):
                queriedPosts = allMyPosts.stream()
                        .filter(p -> p.isActive() && p.getModerationStatus().equals(ModerationStatus.ACCEPTED))
                        .collect(Collectors.toCollection(ArrayList::new));
                break;
        }
        ArrayList<Post> postsToShow = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            int index = offset + i;
            if (index == queriedPosts.size()) {
                break;
            }
            postsToShow.add(queriedPosts.get(index));
        }
        ResponseAPI responseAPI = new ResponseMyPosts(postsToShow.size(), postsToShow);
        return new ResponseEntity<>(responseAPI, HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> post(String timeString, byte active, String title, String text, String tags,
                                            HttpSession session) {
        LocalDateTime time = null;
        try {
            time = LocalDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (title.equals("") || text.equals("") || title.length() < 10 || text.length() < 500) {
            return new ResponseEntity<>(new ResponseFailPost(), HttpStatus.BAD_REQUEST);
        }
        if (time == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // время не распознано
        }
        if (time.isBefore(LocalDateTime.now())) {
            time = LocalDateTime.now();
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        String[] tagsSplit = tags.split(",");
        boolean isActive = false;
        if (active == 1) {
            isActive = true;
        }
        Timestamp timestamp = Timestamp.from(time.toInstant(ZoneOffset.UTC));
        Post currentPost = postRepository.save(new Post(isActive, ModerationStatus.NEW, user, timestamp, title, text));
        for (String currentTagName : tagsSplit) {
            if (!currentTagName.isBlank()) {
                Tag currentTag = tagRepositoryService.addTag(new Tag(currentTagName));
                tagToPostRepositoryService.addTagToPost(new TagToPost(currentTag, currentPost)); // Возможно надо добавлять в Set у тегов и у постов
            }
        }
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
    }

    public ResponseEntity<String> uploadImage(MultipartFile image, HttpSession session) {
        if (image == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!Files.exists(Path.of(ROOT_PATH_TO_UPLOAD))) {
            try {
                Files.createDirectory(Path.of(ROOT_PATH_TO_UPLOAD));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fullDestPath;
        File newFile = null;
        do {
            fullDestPath = getPathStringToUpload();
            if (!Files.exists(Path.of(fullDestPath))) {
                newFile = new File(fullDestPath);
            }
        } while (newFile == null);
        copyFile(image, newFile);
        // TODO сделать как-то загрузку файла на сервер, а не в папку
        return new ResponseEntity<>(fullDestPath, HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> editPost(int id, String timeString, byte active, String title, String text,
                                                String tags, HttpSession session) {
        LocalDateTime time = null;
        try {
            time = LocalDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Post currentPost = getPostById(id);
        if (currentPost == null || title.equals("") || text.equals("") || title.length() < 10 || text.length() < 500) {
            return new ResponseEntity<>(new ResponseFailPost(), HttpStatus.BAD_REQUEST);
        }
        if (time == null || time.isBefore(LocalDateTime.now())) {
            time = LocalDateTime.now();
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        String[] tagsSplit = tags.split(",");
        boolean isActive = false;
        if (active == 1) {
            isActive = true;
        }
        Timestamp timestamp = Timestamp.from(time.toInstant(ZoneOffset.UTC));
        currentPost.setActive(isActive);
        currentPost.setTime(timestamp);
        currentPost.setTitle(title);
        currentPost.setText(text);
        currentPost.setModerationStatus(ModerationStatus.NEW);
        postRepository.save(currentPost); // Пост пересохранили, меняем теги и связи с тегами, удаляя старые и добавляя новые
        currentPost.getTagsToPostsSet().forEach(tag2post -> {
            tagRepositoryService.deleteTag(tag2post.getTag());
            tagToPostRepositoryService.deleteTagToPost(tag2post);
        }); // Нужно ли отдельно удалять все теги?
        for (String currentTagName : tagsSplit) {         // удалили все теги и тег-ту-посты, добавляем новые
            if (!currentTagName.isBlank()) {
                Tag currentTag = tagRepositoryService.addTag(new Tag(currentTagName));
                tagToPostRepositoryService.addTagToPost(new TagToPost(currentTag, currentPost)); // Возможно надо добавлять в Set у тегов и у постов
            }
        }
        return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
    }

    public ResponseEntity<ResponseAPI> moderatePost(int postId, String decision, HttpSession session) {
        // Если пользователь залогинен
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Пользователь не найден, хотя сессия есть
        }
        if (!user.isModerator()) {  // Не модератор
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Post post = getPostById(postId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Пост не найден
        }
        decision = decision.toUpperCase().trim();
        if (!decision.equals("DECLINE") && !decision.equals("ACCEPT")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Результат модерации не распознан
        }
        post.setModeratorId(userId);
        if (decision.equals("DECLINE")) {
            post.setModerationStatus(ModerationStatus.DECLINED);
        } else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    public ResponseEntity<ResponseAPI> countPostsByYear(Integer year) {
        ArrayList<Post> allPosts = getAllPosts();
        HashMap<Date, Integer> postsCountByDate = new HashMap<>();
        TreeSet<Integer> allYears = new TreeSet<>();
        int queriedYear;
        if (year == null) {
            queriedYear = LocalDateTime.now().getYear();
        } else {
            queriedYear = year;
        }
        for (Post p : allPosts) {
            Integer postYear = p.getTime().toLocalDateTime().getYear();
            allYears.add(postYear);
            if (postYear.equals(queriedYear)) {
                Date postDate = Date.valueOf(p.getTime().toLocalDateTime().toLocalDate());
                Integer postCount = postsCountByDate.getOrDefault(postDate, 0);
                postsCountByDate.put(postDate, postCount + 1);
            }
        }
        return new ResponseEntity<ResponseAPI>(new ResponsePostsCalendar(postsCountByDate, allYears), HttpStatus.OK);
    }

    @Override
    public int getModerationCount(int moderatorUserId) { // TODO удалить, храним данные по модерируемым постам в сете в объекте User
        AtomicInteger count = new AtomicInteger();
        postRepository.findAll().forEach(p -> {
            if (p.getModeratorId() == moderatorUserId) {
                count.addAndGet(1);
            }
        });
        return count.intValue();
    }

    @Override
    public ArrayList<Post> getAllPosts() {
        ArrayList<Post> posts = new ArrayList<>();
        postRepository.findAll().forEach(posts::add);
        return posts;
    }

    private JSONObject createResultJsonObjectByPost(Post p) {
        String timeString = getTimeString(p.getTime().toLocalDateTime());
        JSONObject postObject = new JSONObject();
        postObject.put("id", p.getId()).put("time", timeString);
        JSONObject userObject = new JSONObject();
        userObject.put("id", p.getUser().getId()).put("name", p.getUser().getName());
        postObject.put("user", userObject).put("title", p.getTitle())
                .put("announce", "Текст анонса поста без HTML-тэгов") //TODO откуда брать???
                .put("likeCount", p.getPostVotes().stream().filter(l -> l.getValue() == 1).count())
                .put("dislikeCount", p.getPostVotes().stream().filter(l -> l.getValue() == -1).count())
                .put("commentCount", p.getPostComments().size()).put("viewCount", p.getViewCount());
        return postObject;
    }

    private String getTimeString(LocalDateTime objectCreatedTime) {
        StringBuilder timeString = new StringBuilder();
        if (objectCreatedTime.isAfter(LocalDate.now().atStartOfDay())
                && objectCreatedTime.isBefore(LocalDateTime.now())) {
            timeString.append("Сегодня, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else if (objectCreatedTime.isAfter(LocalDate.now().atStartOfDay().minusDays(1))) {
            timeString.append("Вчера, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            timeString.append(objectCreatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm")));
        }
        return timeString.toString();
    }

    private void copyFile(MultipartFile source, File dest) {
        try {
            byte[] bytes = source.getBytes();
            Files.write(dest.toPath(), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try (InputStream inputStream = new FileInputStream(source);
//        OutputStream outputStream = new FileOutputStream(dest)) { // Autocloseable потоки //TODO переделать на потоки?
//            byte[] bytes = source.getBytes();
//            int length = bytes.length;
//            while ((length = inputStream.read(bytes)) > 0) {
//                outputStream.write(bytes, 0, length);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private String getPathStringToUpload() {
        String randomHash = String.valueOf(String.valueOf(Math.pow(Math.random(), 100 * Math.random())).hashCode());
        String firstFolder = randomHash.substring(0, randomHash.length() / 6);
        String secondFolder = randomHash.substring(firstFolder.length(), firstFolder.length() + randomHash.length() / 6);
        String thirdFolder = randomHash.substring(secondFolder.length(), secondFolder.length() + randomHash.length() / 6);
        String fileName = randomHash.substring(thirdFolder.length());
        StringBuilder builder = new StringBuilder("/upload/").append(firstFolder).append("/")
                .append(secondFolder).append("/").append(thirdFolder).append("/")
                .append(fileName).append(".jpg");
        String pathToUpload = builder.toString();
        return "/" + ROOT_PATH_TO_UPLOAD + pathToUpload;
    }
}
