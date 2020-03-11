package main.services.Impl;

import main.model.ModerationStatus;
import main.model.entities.Post;
import main.model.entities.User;
import main.model.repositories.PostRepository;
import main.services.interfaces.PostRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PostRepositoryServiceImpl implements PostRepositoryService {

    @Autowired
    private PostRepository postRepository;

    public ResponseEntity<?> getPost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.isPresent() ?
                new ResponseEntity<>(optionalPost.get(), HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    public Post getPostById(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.isPresent() ?
                optionalPost.get() : null;
    }

    public ResponseEntity getPostsWithParams(int offset, int limit, String mode) {
//        ArrayList<Post> posts = new ArrayList<>();
//        postRepository.findAll().forEach(posts::add);
        return null; // new ResponseEntity<>(posts, HttpStatus.OK);
    }

    public ResponseEntity searchPosts(int offset, String query, int limit) {
        return null;
    }

    public ResponseEntity getPostsByDate(Date date, int offset, int limit) {
        return null;
    }

    public ResponseEntity getPostsByTag(int limit, String tag, int offset) {
        return null;
    }

    public ResponseEntity getPostsForModeration(String status, int offset, int limit) {
        return null;
    }

    public ResponseEntity getMyPosts(String status, int offset, int limit) {
        return null;
    }

    public ResponseEntity post(Time time, byte active, String title, String text, String tags) {
        return null;
    }

    public ResponseEntity uploadImage(File image) {
        return null;
    }

    public ResponseEntity editPost(int id, Time time, byte active, String title, String text, String tags) {
        return null;
    }

    public ResponseEntity<String> moderatePost(int postId, String decision, HttpSession session, UserRepositoryService userRepositoryService) {
        // Если пользователь залогинен
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Пользователь не найден, хотя сессия есть
        }
        if (!user.isModerator()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } // Не модератор
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
    public int getModerationCount(int moderatorUserId) {
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
}
