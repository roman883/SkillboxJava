package main.services.interfaces;

import main.api.request.PostVoteRequest;
import main.api.response.ResponseApi;
import main.model.entities.PostVote;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

public interface PostVoteRepositoryService {

    ResponseEntity<ResponseApi> likePost(PostVoteRequest postVoteRequest, HttpSession session);

    ResponseEntity<ResponseApi> dislikePost(PostVoteRequest postVoteRequest, HttpSession session);

    HashSet<PostVote> getAllPostVotes();
}
