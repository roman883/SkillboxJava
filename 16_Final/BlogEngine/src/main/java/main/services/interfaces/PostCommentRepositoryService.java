package main.services.interfaces;

import main.model.entities.PostComment;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public interface PostCommentRepositoryService {

    ArrayList<PostComment> getAllComments();

    ResponseEntity<?> addComment(int parentId, int postId, String text, HttpSession session,
                              UserRepositoryService userRepoService, PostRepositoryService postRepositoryService);
}
