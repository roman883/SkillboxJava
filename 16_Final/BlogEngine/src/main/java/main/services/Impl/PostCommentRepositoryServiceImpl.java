package main.services.Impl;

import main.api.request.AddCommentRequest;
import main.api.response.ResponseApi;
import main.model.entities.Post;
import main.model.entities.PostComment;
import main.model.entities.User;
import main.model.repositories.PostCommentRepository;
import main.api.response.ResponseFailComment;
import main.api.response.*;
import main.services.interfaces.PostCommentRepositoryService;
import main.services.interfaces.PostRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostCommentRepositoryServiceImpl implements PostCommentRepositoryService {

    private static final int DEFAULT_COMMENT_LENGTH = 200;

    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private UserRepositoryService userRepoService;
    @Autowired
    private PostRepositoryService postRepositoryService;

    @Override
    public ArrayList<PostComment> getAllComments() {
        ArrayList<PostComment> resultList = new ArrayList<>();
        postCommentRepository.findAll().forEach(resultList::add);
        return resultList;
    }

    @Override
    public ResponseEntity<ResponseApi> addComment(AddCommentRequest addCommentRequest, HttpSession session) {
        Integer parentId = addCommentRequest.getParentId();
        Integer postId = addCommentRequest.getPostId();
        if (parentId == null && postId == null) { // не найдены id
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String text = addCommentRequest.getText();
        if (text.equals("") || text.length() < DEFAULT_COMMENT_LENGTH) {
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        User user = userRepoService.getUser(userId).getBody();
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        PostComment newComment = postCommentRepository.save(new PostComment(parentComment, user, parentPost, time, text));
        return new ResponseEntity<>(new ResponseSuccessComment(newComment), HttpStatus.OK);
    }

    @Override
    public PostComment getPostCommentById(int id) {
        Optional<PostComment> optionalComment = postCommentRepository.findById(id);
        return optionalComment.isPresent() ? optionalComment.get() : null;
    }
}
