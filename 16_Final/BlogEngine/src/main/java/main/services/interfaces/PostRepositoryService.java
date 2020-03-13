package main.services.interfaces;

import main.model.entities.Post;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;

public interface PostRepositoryService {

    ResponseEntity<String> getPost(int id, TagRepositoryService tagRepositoryService,
                           PostCommentRepositoryService postCommentRepositoryService,
                           PostVoteRepositoryService postVoteRepositoryService);

    ResponseEntity<String> getPostsWithParams(int offset, int limit, String mode);

    ResponseEntity<String> searchPosts(int offset, String query, int limit);

    ResponseEntity<String> getPostsByDate(Date date, int offset, int limit);

    ResponseEntity<String> getPostsByTag(int limit, String tag, int offset, TagRepositoryService tagRepositoryService);

    ResponseEntity<String> getPostsForModeration(String status, int offset, int limit, HttpSession session,
                                         UserRepositoryService userRepositoryService);

    ResponseEntity<String> getMyPosts(String status, int offset, int limit, HttpSession session,
                                      UserRepositoryService userRepositoryService);

    ResponseEntity<String> post(String time, byte active, String title, String text, String tags, HttpSession session,
                        UserRepositoryService userRepositoryService, TagRepositoryService tagRepositoryService,
                        TagToPostRepositoryService tagToPostRepositoryService) throws ParseException;

    ResponseEntity<String> uploadImage(File image, HttpSession session, UserRepositoryService userRepositoryService);

    ResponseEntity<String> editPost(int id, String time, byte active, String title, String text, String tags, HttpSession session,
                            UserRepositoryService userRepositoryService, TagRepositoryService tagRepositoryService,
                            TagToPostRepositoryService tagToPostRepositoryService);

    ResponseEntity<String> moderatePost(int postId, String decision, HttpSession session, UserRepositoryService userRepositoryService);

    ResponseEntity<String> countPostsByYear(Integer year);

    int getModerationCount(int moderatorUserId);

    ArrayList<Post> getAllPosts();

    Post getPostById(int postId);
}