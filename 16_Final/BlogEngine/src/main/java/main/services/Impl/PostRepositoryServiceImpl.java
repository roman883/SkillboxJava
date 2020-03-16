package main.services.Impl;

import main.model.ModerationStatus;
import main.model.entities.*;
import main.model.repositories.PostRepository;
import main.model.responses.ResponseAPI;
import main.model.responses.ResponsePost;
import main.services.interfaces.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PostRepositoryServiceImpl implements PostRepositoryService {

    @Autowired
    private PostRepository postRepository;


    public ResponseEntity<ResponseAPI> getPost(int id) {
        Post post = getPostById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!post.isActive() || !post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                || post.getTime().toLocalDateTime().isBefore(LocalDateTime.now())) {
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

    public ResponseEntity<String> getPostsWithParams(int offset, int limit, String mode) { //TODO проверки входных значений?
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
        JSONObject result = new JSONObject();
        JSONArray postsArray = new JSONArray();
        for (Post p : postsToShow) {
            JSONObject postObject = createResultJsonObjectByPost(p);
            postsArray.put(postObject);
        }
        result.put("count", count).put("posts", postsArray);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> searchPosts(int offset, String query, int limit) {
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
        JSONObject result = new JSONObject();
        JSONArray postsArray = new JSONArray();
        for (Post p : postsToShow) {
            JSONObject postObject = createResultJsonObjectByPost(p);
            postsArray.put(postObject);
        }
        result.put("count", count).put("posts", postsArray);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getPostsByDate(Date date, int offset, int limit) {
        // TODO дата может приходить как строка “2019-10-15”
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
                    && currentPost.getTime().toLocalDateTime().toLocalDate().isEqual(date.toLocalDate())) {
                postsToShow.add(currentPost);
            }
        }
        JSONArray postsArray = new JSONArray();
        for (Post post : postsToShow) {
            postsArray.put(createResultJsonObjectByPost(post));
        }
        JSONObject result = new JSONObject();
        result.put("count", postsToShow.size()).put("posts", postsArray);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getPostsByTag(int limit, String tag, int offset, TagRepositoryService tagRepositoryService) {
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
        JSONArray postsArray = new JSONArray();
        for (Post post : postsToShow) {
            postsArray.put(createResultJsonObjectByPost(post));
        }
        JSONObject result = new JSONObject();
        result.put("count", postsToShow.size()).put("posts", postsArray);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getPostsForModeration(String status, int offset, int limit, HttpSession session,
                                                        UserRepositoryService userRepositoryService) {
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
        JSONArray postsArray = new JSONArray();
        for (Post post : postsToShow) {
            JSONObject tempPostObject = createResultJsonObjectByPost(post);
            tempPostObject.remove("dislikeCount");
            tempPostObject.remove("commentCount");
            tempPostObject.remove("likeCount");
            tempPostObject.remove("viewCount");
            postsArray.put(tempPostObject);
        }
        JSONObject result = new JSONObject();
        result.put("count", postsToShow.size()).put("posts", postsArray);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> getMyPosts(String status, int offset, int limit, HttpSession session,
                                             UserRepositoryService userRepositoryService) {
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
        JSONArray postsArray = new JSONArray();
        for (Post post : postsToShow) {
            JSONObject tempPostObject = createResultJsonObjectByPost(post);
            tempPostObject.remove("user");
            postsArray.put(tempPostObject);
        }
        JSONObject result = new JSONObject();
        result.put("count", postsToShow.size()).put("posts", postsArray);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> post(String timeString, byte active, String title, String text, String tags, HttpSession session,
                                       UserRepositoryService userRepositoryService, TagRepositoryService tagRepositoryService,
                                       TagToPostRepositoryService tagToPostRepositoryService) { //TODO time может быть строкой
        LocalDateTime time = null;
        try {
            time = LocalDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (title.equals("") || text.equals("") || title.length() < 10 || text.length() < 500) {
            JSONObject falseResult = new JSONObject();
            JSONObject errorsObject = new JSONObject();
            errorsObject.put("title", "Заголовок не установлен").put("text", "Текст публикации слишком короткий");
            falseResult.put("result", false).put("errors", errorsObject);
            return new ResponseEntity<>(falseResult.toString(), HttpStatus.BAD_REQUEST);
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
        JSONObject resultString = new JSONObject();
        resultString.put("result", true);
        return new ResponseEntity<>(resultString.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> uploadImage(File image, HttpSession session, UserRepositoryService userRepositoryService) {
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
        String randomHash = String.valueOf(String.valueOf(Math.pow(Math.random(), 100 * Math.random())).hashCode());
        String firstFolder = randomHash.substring(0, randomHash.length() / 6);
        String secondFolder = randomHash.substring(firstFolder.length(), firstFolder.length() + randomHash.length() / 6);
        String thirdFolder = randomHash.substring(secondFolder.length(), secondFolder.length() + randomHash.length() / 6);
        String fileName = randomHash.substring(thirdFolder.length());
        StringBuilder builder = new StringBuilder("/upload/").append(firstFolder).append("/")
                .append(secondFolder).append("/").append(thirdFolder).append("/")
                .append(fileName).append(".jpg");
        String pathToUpload = builder.toString();
        // TODO сделать как-то загрузку файла на сервер
        return new ResponseEntity<>(pathToUpload, HttpStatus.OK);
    }

    public ResponseEntity<String> editPost(int id, String timeString, byte active, String title, String text, String tags,
                                   HttpSession session, UserRepositoryService userRepositoryService,
                                   TagRepositoryService tagRepositoryService,
                                   TagToPostRepositoryService tagToPostRepositoryService) {
        LocalDateTime time = null;
        try {
            time = LocalDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Post currentPost = getPostById(id);
        if (currentPost == null || title.equals("") || text.equals("") || title.length() < 10 || text.length() < 500) {
            JSONObject falseResult = new JSONObject();
            JSONObject errorsObject = new JSONObject();
            errorsObject.put("title", "Заголовок не установлен").put("text", "Текст публикации слишком короткий");
            falseResult.put("result", false).put("errors", errorsObject);
            return new ResponseEntity<>(falseResult.toString(), HttpStatus.BAD_REQUEST);
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
        JSONObject resultString = new JSONObject();
        resultString.put("result", true);
        return new ResponseEntity<>(resultString.toString(), HttpStatus.OK);
    }

    public ResponseEntity<String> moderatePost(int postId, String decision, HttpSession session,
                                               UserRepositoryService userRepositoryService) {
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

    public ResponseEntity<String> countPostsByYear(Integer year) {
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
        JSONObject result = new JSONObject();
        JSONArray yearsArray = new JSONArray();
        allYears.forEach(yearsArray::put);
        JSONObject postsObject = new JSONObject();
        for (Date d : postsCountByDate.keySet()) {
            postsObject.put(String.valueOf(d), postsCountByDate.get(d));
        }
        result.put("years", yearsArray).put("posts", postsObject);
        return new ResponseEntity<String>(result.toString(), HttpStatus.OK); //TODO проблема с получением в ResponseEntity объекто JSONObject, приходится возвращать json.toString()
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
        if (objectCreatedTime.isAfter(LocalDateTime.now().minusDays(1))) {
            timeString.append("Сегодня, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else if (objectCreatedTime.isAfter(LocalDateTime.now().minusDays(2))) {
            timeString.append("Вчера, ").append(objectCreatedTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            timeString.append(objectCreatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm")));
        }
        return timeString.toString();
    }
}
