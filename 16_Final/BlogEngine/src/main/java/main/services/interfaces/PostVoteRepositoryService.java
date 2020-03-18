package main.services.interfaces;

import main.api.response.ResponseApi;
import main.model.entities.PostVote;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

public interface PostVoteRepositoryService {

    ResponseEntity<ResponseApi> likePost(int postId, HttpSession session);

    ResponseEntity<ResponseApi> dislikePost(int postId, HttpSession session);

    HashSet<PostVote> getAllPostVotes();
}
