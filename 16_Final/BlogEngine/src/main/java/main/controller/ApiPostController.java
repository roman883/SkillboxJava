package main.controller;

import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.ResponseApi;
import main.services.Impl.*;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@ComponentScan("service")
public class ApiPostController {

    private final PostRepositoryService postRepoService;
    private final PostVoteRepositoryService postVoteRepoService;

    @Autowired
    public ApiPostController(PostRepositoryServiceImpl postRepoServiceImpl,
                             PostVoteRepositoryServiceImpl postVoteRepoServiceImpl) {
        this.postRepoService = postRepoServiceImpl;
        this.postVoteRepoService = postVoteRepoServiceImpl;
    }

    @GetMapping(value = "/api/post", params = {"offset", "limit", "mode"})
    // или /posts/? рекомендуется во множественном числе
    public @ResponseBody
    ResponseEntity<ResponseApi> get(@RequestParam(value = "offset") int offset,  // Может иметь defaultValue = "10"
                                    @RequestParam(value = "limit") int limit,
                                    @RequestParam(value = "mode") String mode) {
        return postRepoService.getPostsWithParams(offset, limit, mode);
    }

    @GetMapping(value = "/api/post/search", params = {"offset", "limit", "query"})
    public @ResponseBody
    ResponseEntity<ResponseApi> searchPosts(@RequestParam(value = "offset") int offset,
                                            @RequestParam(value = "limit") int limit,
                                            @RequestParam(value = "query") String query) {
        return postRepoService.searchPosts(offset, query, limit);
    }

    @GetMapping(value = "/api/post/{id}")
    public @ResponseBody
    ResponseEntity<ResponseApi> get(@PathVariable int id) {
        return postRepoService.getPost(id);
    }

    @GetMapping(value = "/api/post/byDate", params = {"offset", "limit", "date"})
    public @ResponseBody
    ResponseEntity<ResponseApi> getPostsByDate(@RequestParam(value = "offset") int offset,
                                               @RequestParam(value = "limit") int limit,
                                               @RequestParam(value = "date") String date) {
        return postRepoService.getPostsByDate(date, offset, limit);
    }

    @GetMapping(value = "/api/post/byTag", params = {"tag", "offset", "limit"})
    public @ResponseBody
    ResponseEntity<ResponseApi> get(@RequestParam(value = "limit") int limit,
                                    @RequestParam(value = "tag") String tag,
                                    @RequestParam(value = "offset") int offset) {
        return postRepoService.getPostsByTag(limit, tag, offset);
    }

    @GetMapping(value = "/api/post/moderation", params = {"status", "offset", "limit"})
    public @ResponseBody
    ResponseEntity<ResponseApi> getPostsForModeration(@RequestParam(value = "status") String status,
                                                      @RequestParam(value = "offset") int offset,
                                                      @RequestParam(value = "limit") int limit,
                                                      HttpServletRequest request) {
        return postRepoService.getPostsForModeration(status, offset, limit, request.getSession());
    }

    @GetMapping(value = "/api/post/my", params = {"status", "offset", "limit"})
    public @ResponseBody
    ResponseEntity<ResponseApi> getMyPosts(@RequestParam(value = "status") String status,
                                           @RequestParam(value = "offset") int offset,
                                           @RequestParam(value = "limit") int limit,
                                           HttpServletRequest request) {
        return postRepoService.getMyPosts(status, offset, limit, request.getSession());
    }

    @PostMapping(value = "/api/post")
    public @ResponseBody
    ResponseEntity<ResponseApi> post(@RequestBody PostRequest postRequest,
                                     HttpServletRequest request) throws ParseException {
        return postRepoService.post(postRequest, request.getSession());
    }

    @PutMapping(value = "/api/post/{id}")
    public @ResponseBody
    ResponseEntity<ResponseApi> editPost(@PathVariable int id,
                                         @RequestBody PostRequest postRequest,
                                         HttpServletRequest request) {
        return postRepoService.editPost(id, postRequest, request.getSession());
    }

    @PostMapping(value = "/api/post/like")
    public @ResponseBody
    ResponseEntity<ResponseApi> likePost(@RequestBody PostVoteRequest postVoteRequest, HttpServletRequest request) {
        return postVoteRepoService.likePost(postVoteRequest, request.getSession());
    }

    @PostMapping(value = "/api/post/dislike")
    public @ResponseBody
    ResponseEntity<ResponseApi> dislikePost(@RequestBody PostVoteRequest postVoteRequest,
                                            HttpServletRequest request) {
        return postVoteRepoService.dislikePost(postVoteRequest, request.getSession());
    }
}
