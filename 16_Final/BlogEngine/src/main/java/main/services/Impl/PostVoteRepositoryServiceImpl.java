package main.services.Impl;

import main.model.entities.PostVote;
import main.model.repositories.PostVoteRepository;
import main.services.interfaces.PostVoteRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class PostVoteRepositoryServiceImpl implements PostVoteRepositoryService {

    @Autowired
    private PostVoteRepository postVoteRepository;

    @Override
    public ResponseEntity likePost(int postId) {
        return null;
    }

    @Override
    public ResponseEntity dislikePost(int postId) {
        return null;
    }

    @Override
    public HashSet<PostVote> getAllPostVotes() {
        HashSet<PostVote> postVotes = new HashSet<>();
        postVoteRepository.findAll().forEach(postVotes::add);
        return postVotes;
    }
}
