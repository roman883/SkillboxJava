package main.services.interfaces;

import main.model.entities.PostVote;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

public interface PostVoteRepositoryService {

    ResponseEntity<String> likePost(int postId, HttpSession session, UserRepositoryService userRepositoryService,
                            PostRepositoryService postRepositoryService);

    ResponseEntity<String> dislikePost(int postId, HttpSession session, UserRepositoryService userRepositoryService,
                               PostRepositoryService postRepositoryService);

    HashSet<PostVote> getAllPostVotes();
}
