package main.services.Impl;

import lombok.extern.slf4j.Slf4j;
import main.api.request.PostVoteRequest;
import main.api.response.ResponseApi;
import main.api.response.ResponseBoolean;
import main.model.entities.Post;
import main.model.entities.PostVote;
import main.model.entities.User;
import main.model.repositories.PostVoteRepository;
import main.services.interfaces.PostRepositoryService;
import main.services.interfaces.PostVoteRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashSet;

@Slf4j
@Service
public class PostVoteRepositoryServiceImpl implements PostVoteRepositoryService {

    @Autowired
    private PostVoteRepository postVoteRepository;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private PostRepositoryService postRepositoryService;

    @Override
    public ResponseEntity<ResponseApi> likePost(PostVoteRequest postVoteRequest, HttpSession session) {
        int postId = postVoteRequest.getPostId();
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.UNAUTHORIZED);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.INTERNAL_SERVER_ERROR); // Ошибка, пользователь не найден, а сессия есть
        }
        // проверяем лайкал ли юзер ранее этот пост
        Post currentPost = postRepositoryService.getPostById(postId);
        ResponseEntity<ResponseApi> response =
                new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        if (currentPost == null) {
            log.warn("--- Не найден пост с id: " + postId);
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST); // Ошибка. Поста с таким id нет
        }
        PostVote beforeLike = postVoteRepository.getPostVoteByUserIdAndPostId(postId, userId);
//        Set<PostVote> currentUserVotes = user.getPostVotes(); // Вместо этого получаем лайки из БД
//        for (PostVote p : currentUserVotes) {
//            if (p.getPost().getId() == postId) {
//                beforeLike = p;
//                break;
//            }
//        }
        if (beforeLike == null) { // Не было лайков и диз
            PostVote newLike = postVoteRepository
                    .save(new PostVote(user, currentPost, LocalDateTime.now(), (byte) 1));
            log.info("--- Создан лайк поста с id:" + postId + ", пользователем с id:" + userId + ", postVoteID:" + newLike.getId());
            response = new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        } else if (beforeLike.getValue() == 1) { // Повторный лайк
            log.info("--- Повторный лайк поста с id:" + postId + ", пользователем с id:" + userId);
            response = new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        } else if (beforeLike.getValue() == -1) { // был дизлайк, удаляем
            postVoteRepository.delete(beforeLike);
            log.info("--- Удален дизлайк поста с id:" + postId + ", пользователем с id:" + userId);
            response = new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        }
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    @Override
    public ResponseEntity<ResponseApi> dislikePost(PostVoteRequest postVoteRequest, HttpSession session) {
        int postId = postVoteRequest.getPostId();
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.UNAUTHORIZED);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.INTERNAL_SERVER_ERROR); // Ошибка, пользователь не найден, а сессия есть
        }
        Post currentPost = postRepositoryService.getPostById(postId);
        ResponseEntity<ResponseApi> response =
                new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        if (currentPost == null) {
            log.warn("--- Не найден пост с id: " + postId);
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST); // Ошибка
        }
        PostVote beforeLike = postVoteRepository.getPostVoteByUserIdAndPostId(postId, userId);
        if (beforeLike == null) { // Не было лайков и диз
            PostVote newDislike = postVoteRepository
                    .save(new PostVote(user, currentPost, LocalDateTime.now(), (byte) -1));
            log.info("--- Создан дизлайк поста с id:" + postId + ", пользователем с id:" + userId + ", postVoteID:" + newDislike.getId());
            response = new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        } else if (beforeLike.getValue() == -1) { // Повторный дизлайк
            log.info("--- Повторный дизлайк поста с id:" + postId + ", пользователем с id:" + userId);
            response = new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        } else if (beforeLike.getValue() == 1) { // был лайк, удаляем
            postVoteRepository.delete(beforeLike);
            log.info("--- Удален лайк поста с id:" + postId + ", пользователем с id:" + userId);
            response = new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        }
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    @Override
    public HashSet<PostVote> getAllPostVotes() {
        HashSet<PostVote> postVotes = new HashSet<>();
        postVoteRepository.findAll().forEach(postVotes::add);
        return postVotes;
    }
}
