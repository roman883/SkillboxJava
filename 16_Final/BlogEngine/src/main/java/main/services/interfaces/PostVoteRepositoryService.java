package main.services.interfaces;

import main.model.entities.PostVote;
import main.model.responses.ResponseAPI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

public interface PostVoteRepositoryService {

    ResponseEntity<ResponseAPI> likePost(int postId, HttpSession session);

    ResponseEntity<ResponseAPI> dislikePost(int postId, HttpSession session);

    HashSet<PostVote> getAllPostVotes();
}
