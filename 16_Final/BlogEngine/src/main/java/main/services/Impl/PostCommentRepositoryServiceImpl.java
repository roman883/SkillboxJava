package main.services.Impl;

import main.api.request.AddCommentRequest;
import main.api.response.*;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.User;
import main.model.repositories.PostCommentRepository;
import main.services.interfaces.PostCommentRepositoryService;
import main.services.interfaces.PostRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostCommentRepositoryServiceImpl implements PostCommentRepositoryService {

    @Value("${post_comment.min_length}")
    private int minCommentLength;
    @Value("${post_comment.max_length}")
    private int maxCommentLength;

    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private UserRepositoryService userRepoService;
    @Autowired
    private PostRepositoryService postRepositoryService;

    @Override
    public ArrayList<PostComment> getAllComments() {
        return new ArrayList<>(postCommentRepository.findAll());
    }

    @Override
    public ResponseEntity<ResponseApi> addComment(AddCommentRequest addCommentRequest, HttpSession session) {
        Integer parentId = addCommentRequest.getParentId();
        Integer postId = addCommentRequest.getPostId();
        if (parentId == null && postId == null) {
            return new ResponseEntity<>(new ResponseBadReqMsg("Не заданы родительский пост и комментарий"), HttpStatus.BAD_REQUEST);
        }
        String text = addCommentRequest.getText();
        if (!isTextValid(text)) {
            return new ResponseEntity<>(new ResponseFailComment(), HttpStatus.OK);
        } // Если все ок, создаем комментарий и возвращаем id
        Integer userId = userRepoService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Ошибка, не авторизован
        }
        PostComment parentComment = null;
        Post parentPost = null;
        if (parentId != null) {
            parentComment = getPostCommentById(parentId);
        }
        if (postId != null) {
            parentPost = postRepositoryService.getPostById(postId);
        }
        if (parentComment == null && parentPost == null) { // не найдены ни пост, ни коммент с таким id на которые добавляем коммент
            return new ResponseEntity<>(new ResponseBadReqMsg("Не найдены родительский пост и комментарий"), HttpStatus.BAD_REQUEST);
        }
        User user = userRepoService.getUser(userId).getBody();
        PostComment newComment = postCommentRepository.save(
                new PostComment(parentComment, user, parentPost, LocalDateTime.now(), text));
        return new ResponseEntity<>(new ResponseSuccessComment(newComment), HttpStatus.OK);
    }

    @Override
    public PostComment getPostCommentById(int id) {
        Optional<PostComment> optionalComment = postCommentRepository.findById(id);
        return optionalComment.isPresent() ? optionalComment.get() : null;
    }

    private boolean isTextValid(String text) {
        return text != null && !text.equals("") && text.length() <= maxCommentLength && text.length() >= minCommentLength;
    }
}
