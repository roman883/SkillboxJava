package main.services.Impl;

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
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.UNAUTHORIZED);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.INTERNAL_SERVER_ERROR); // Ошибка, пользователь не найден, а сессия есть
        }
        // проверяем лайкал ли юзер ранее этот пост
        PostVote beforeLike = postVoteRepository.getPostVoteByUserIdAndPostId(postId, userId);
        Post currentPost = postRepositoryService.getPostById(postId);
        if (currentPost == null) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST); // Ошибка. Поста с таким id нет
        }
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
            return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        }
        if (beforeLike.getValue() == 1) { // Повторный лайк
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        }
        if (beforeLike.getValue() == -1) { // был дизлайк, удаляем
            postVoteRepository.delete(beforeLike);
            return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ResponseApi> dislikePost(PostVoteRequest postVoteRequest, HttpSession session) {
        int postId = postVoteRequest.getPostId();
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.UNAUTHORIZED);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.INTERNAL_SERVER_ERROR); // Ошибка, пользователь не найден, а сессия есть
        }
        // проверяем лайкал ли юзер ранее этот пост
        PostVote beforeLike = postVoteRepository.getPostVoteByUserIdAndPostId(postId, userId);
        Post currentPost = postRepositoryService.getPostById(postId);
        if (currentPost == null) {
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST); // Ошибка
        }
//        Set<PostVote> userVotes = user.getPostVotes(); // Получаем из БД по userId и postId
//        for (PostVote p : userVotes) {
//            if (p.getPost().getId() == postId) {
//                beforeLike = p;
//                break;
//            }
//        }
        if (beforeLike == null) { // Не было лайков и диз
            PostVote newDislike = postVoteRepository
                    .save(new PostVote(user, currentPost, LocalDateTime.now(), (byte) -1));
            return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        }
        if (beforeLike.getValue() == -1) { // Повторный дизлайк
            return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
        }
        if (beforeLike.getValue() == 1) { // был лайк, удаляем
            postVoteRepository.delete(beforeLike);
            return new ResponseEntity<>(new ResponseBoolean(true), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseBoolean(false), HttpStatus.BAD_REQUEST);
    }

    @Override
    public HashSet<PostVote> getAllPostVotes() {
        HashSet<PostVote> postVotes = new HashSet<>();
        postVoteRepository.findAll().forEach(postVotes::add);
        return postVotes;
    }
}
