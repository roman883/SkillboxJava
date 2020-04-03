package main.services.interfaces;

import main.api.request.ModeratePostRequest;
import main.api.request.PostRequest;
import main.api.response.ResponseApi;
import main.model.entities.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public interface PostRepositoryService {

    ResponseEntity<ResponseApi> getPost(int id);

    ResponseEntity<ResponseApi> getPostsWithParams(int offset, int limit, String mode);

    ResponseEntity<ResponseApi> searchPosts(int offset, String query, int limit);

    ResponseEntity<ResponseApi> getPostsByDate(String date, int offset, int limit);

    ResponseEntity<ResponseApi> getPostsByTag(int limit, String tag, int offset);

    ResponseEntity<ResponseApi> getPostsForModeration(String status, int offset, int limit, HttpSession session);

    ResponseEntity<ResponseApi> getMyPosts(String status, int offset, int limit, HttpSession session);

    ResponseEntity<ResponseApi> post(PostRequest postRequest,
                                     HttpSession session) throws ParseException;

    ResponseEntity<?> uploadImage(MultipartFile image, HttpSession session) throws IOException;

    ResponseEntity<ResponseApi> editPost(int id, PostRequest postRequest,
                                    HttpSession session);

    ResponseEntity<ResponseApi> moderatePost(ModeratePostRequest moderatePostRequest, HttpSession session);

    ResponseEntity<ResponseApi> countPostsByYear(Integer year);

//    int getModerationCount(int moderatorUserId);

    ArrayList<Post> getAllPosts();

    Post getPostById(int postId);

    ResponseEntity<ResponseApi> getRecentPosts();
}