package main.services.Impl;

import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.User;
import main.model.repositories.PostCommentRepository;
import main.services.interfaces.PostCommentRepositoryService;
import main.services.interfaces.PostRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class PostCommentRepositoryServiceImpl implements PostCommentRepositoryService {

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Override
    public ArrayList<PostComment> getAllComments() {
        ArrayList<PostComment> resultList = new ArrayList<>();
        postCommentRepository.findAll().forEach(resultList::add);
        return resultList;
    }

    @Override
    public ResponseEntity<?> addComment(int parentId, int postId, String text, HttpSession session,
                                     UserRepositoryService userRepoService, PostRepositoryService postRepositoryService) {
        // проверка авторизации
        Integer userId = userRepoService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Ошибка, не авторизован
        }
        ArrayList<PostComment> comments = getAllComments();
        PostComment parentComment = null;
        for (PostComment comment : comments) {
            if (comment.getId() == parentId) {
                parentComment = comment;
            }
        }
        ArrayList<Post> posts = postRepositoryService.getAllPosts();
        Post parentPost = null;
        for (Post post : posts) {
            if (post.getId() == postId)  {
                parentPost = post;
            }
        }
        if (parentComment == null && parentPost == null) { // не найдены ни пост, ни коммент с таким id
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (text.equals("")) {
            JSONObject json = new JSONObject();
            json.put("result", false);
            JSONObject errorsJson = new JSONObject();
            json.put("errors", errorsJson);
            errorsJson.put("text", "Текст комментария не задан или слишком короткий");
            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        }
        // Если все ок, создаем комментарий и возвращаем id
        User user = userRepoService.getUser(userId).getBody();
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        PostComment newComment = postCommentRepository.save(new PostComment(parentComment, user, parentPost, time));
        JSONObject json = new JSONObject().put("`id`", newComment.getId());
        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }
}
