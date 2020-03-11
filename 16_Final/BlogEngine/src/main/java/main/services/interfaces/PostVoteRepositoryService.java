package main.services.interfaces;

import main.model.entities.PostVote;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;

public interface PostVoteRepositoryService {

    ResponseEntity likePost(int postId);

    ResponseEntity dislikePost(int postId);

    HashSet<PostVote> getAllPostVotes();
}
