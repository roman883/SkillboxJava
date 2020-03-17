package main.services.interfaces;

import main.model.entities.Post;
import main.model.responses.ResponseAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;

public interface PostRepositoryService {

    ResponseEntity<ResponseAPI> getPost(int id);

    ResponseEntity<ResponseAPI> getPostsWithParams(int offset, int limit, String mode);

    ResponseEntity<ResponseAPI> searchPosts(int offset, String query, int limit);

    ResponseEntity<ResponseAPI> getPostsByDate(String date, int offset, int limit);

    ResponseEntity<ResponseAPI> getPostsByTag(int limit, String tag, int offset);

    ResponseEntity<ResponseAPI> getPostsForModeration(String status, int offset, int limit, HttpSession session);

    ResponseEntity<ResponseAPI> getMyPosts(String status, int offset, int limit, HttpSession session);

    ResponseEntity<ResponseAPI> post(String time, byte active, String title, String text, String tags,
                                HttpSession session) throws ParseException;

    ResponseEntity<String> uploadImage(MultipartFile image, HttpSession session) throws IOException;

    ResponseEntity<ResponseAPI> editPost(int id, String time, byte active, String title, String text, String tags,
                                    HttpSession session);

    ResponseEntity<ResponseAPI> moderatePost(int postId, String decision, HttpSession session);

    ResponseEntity<ResponseAPI> countPostsByYear(Integer year);

    int getModerationCount(int moderatorUserId);

    ArrayList<Post> getAllPosts();

    Post getPostById(int postId);
}