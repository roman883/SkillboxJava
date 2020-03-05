package main.service;

import main.model.Post;
import main.model.PostRepository;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostRepositoryService {

    @Autowired
    private PostRepository postRepository;

//    public ResponseEntity<List<Post>> getAllPosts() {
//        ArrayList<Post> posts = new ArrayList<>();
//        postRepository.findAll().forEach(posts::add);
//        return new ResponseEntity<>(posts, HttpStatus.OK);
//    }

    public ResponseEntity<Post> getPost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.isPresent() ?
                new ResponseEntity<>(optionalPost.get(), HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }



//    public ResponseEntity<User> getUser(int id) {
//        Optional<User> optionalUser = userRepository.findById(id);
//        return optionalUser.isPresent() ?
//                new ResponseEntity<>(optionalUser.get(), HttpStatus.OK)
//                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
}
