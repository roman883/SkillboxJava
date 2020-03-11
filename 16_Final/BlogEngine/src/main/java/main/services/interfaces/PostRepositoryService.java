package main.services.interfaces;

import main.model.entities.Post;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public interface PostRepositoryService {

    ResponseEntity getPost(int id);

    ResponseEntity getPostsWithParams(int offset, int limit, String mode);

    ResponseEntity searchPosts(int offset, String query, int limit);

    ResponseEntity getPostsByDate(Date date, int offset, int limit);

    ResponseEntity getPostsByTag(int limit, String tag, int offset);

    ResponseEntity getPostsForModeration(String status, int offset, int limit);

    ResponseEntity getMyPosts(String status, int offset, int limit);

    ResponseEntity post(Time time, byte active, String title, String text, String tags);

    ResponseEntity uploadImage(File image);

    ResponseEntity editPost(int id, Time time, byte active, String title, String text, String tags);

    ResponseEntity<String> moderatePost(int postId, String decision, HttpSession session, UserRepositoryService userRepositoryService);

    ResponseEntity<String> countPostsByYear(Integer year);

    int getModerationCount(int moderatorUserId);

    ArrayList<Post> getAllPosts();
}