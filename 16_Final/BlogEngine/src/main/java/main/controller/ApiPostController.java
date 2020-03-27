package main.controller;

import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.ResponseApi;
import main.services.interfaces.PostRepositoryService;
import main.services.interfaces.PostVoteRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@ComponentScan("service")
public class ApiPostController {

    @Autowired
    private PostRepositoryService postRepoService;
    @Autowired
    private PostVoteRepositoryService postVoteRepoService;

    public ApiPostController() {
    }

    @GetMapping(value = "/api/post", params = {"offset", "limit", "mode"})
    public ResponseEntity<ResponseApi> get(@RequestParam(value = "offset") int offset,  // Может иметь defaultValue = "10"
                                           @RequestParam(value = "limit") int limit,
                                           @RequestParam(value = "mode") String mode) {
        return postRepoService.getPostsWithParams(offset, limit, mode);
    }

    @GetMapping(value = "/api/post/search", params = {"offset", "limit", "query"})
    public ResponseEntity<ResponseApi> searchPosts(@RequestParam(value = "offset") int offset,
                                                   @RequestParam(value = "limit") int limit,
                                                   @RequestParam(value = "query") String query) {
        return postRepoService.searchPosts(offset, query, limit);
    }

    @GetMapping(value = "/api/post/{id}")
    public ResponseEntity<ResponseApi> get(@PathVariable int id) {
        return postRepoService.getPost(id);
    }

    @GetMapping(value = "/api/post/byDate", params = {"offset", "limit", "date"})
    public ResponseEntity<ResponseApi> getPostsByDate(@RequestParam(value = "offset") int offset,
                                                      @RequestParam(value = "limit") int limit,
                                                      @RequestParam(value = "date") String date) {
        return postRepoService.getPostsByDate(date, offset, limit);
    }

    @GetMapping(value = "/api/post/byTag", params = {"tag", "offset", "limit"})
    public ResponseEntity<ResponseApi> get(@RequestParam(value = "limit") int limit,
                                           @RequestParam(value = "tag") String tag,
                                           @RequestParam(value = "offset") int offset) {
        return postRepoService.getPostsByTag(limit, tag, offset);
    }

    @GetMapping(value = "/api/post/moderation", params = {"status", "offset", "limit"})
    public ResponseEntity<ResponseApi> getPostsForModeration(@RequestParam(value = "status") String status,
                                                             @RequestParam(value = "offset") int offset,
                                                             @RequestParam(value = "limit") int limit,
                                                             HttpServletRequest request) {
        return postRepoService.getPostsForModeration(status, offset, limit, request.getSession());
    }

    @GetMapping(value = "/api/post/my", params = {"status", "offset", "limit"})
    public ResponseEntity<ResponseApi> getMyPosts(@RequestParam(value = "status") String status,
                                                  @RequestParam(value = "offset") int offset,
                                                  @RequestParam(value = "limit") int limit,
                                                  HttpServletRequest request) {
        return postRepoService.getMyPosts(status, offset, limit, request.getSession());
    }

    @PostMapping(value = "/api/post")
    public ResponseEntity<ResponseApi> post(@RequestBody PostRequest postRequest,
                                            HttpServletRequest request) throws ParseException {
        return postRepoService.post(postRequest, request.getSession());
    }

    @PutMapping(value = "/api/post/{id}")
    public ResponseEntity<ResponseApi> editPost(@PathVariable int id,
                                                @RequestBody PostRequest postRequest,
                                                HttpServletRequest request) {
        return postRepoService.editPost(id, postRequest, request.getSession());
    }

    @PostMapping(value = "/api/post/like")
    public ResponseEntity<ResponseApi> likePost(@RequestBody PostVoteRequest postVoteRequest, HttpServletRequest request) {
        return postVoteRepoService.likePost(postVoteRequest, request.getSession());
    }

    @PostMapping(value = "/api/post/dislike")
    public ResponseEntity<ResponseApi> dislikePost(@RequestBody PostVoteRequest postVoteRequest,
                                                   HttpServletRequest request) {
        return postVoteRepoService.dislikePost(postVoteRequest, request.getSession());
    }
}
