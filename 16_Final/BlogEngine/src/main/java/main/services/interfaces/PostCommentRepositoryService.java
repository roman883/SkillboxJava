package main.services.interfaces;

import main.model.entities.PostComment;
import main.model.responses.ResponseAPI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public interface PostCommentRepositoryService {

    ArrayList<PostComment> getAllComments();

    ResponseEntity<ResponseAPI> addComment(Integer parentId, Integer postId, String text, HttpSession session);

    PostComment getPostCommentById(int id);
}
