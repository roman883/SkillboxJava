package main.controller;

import main.model.responses.ResponseAPI;
import main.services.Impl.*;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.text.ParseException;

@RestController
@ComponentScan("service")
public class ApiPostController {

    private final PostRepositoryService postRepoService;
    private final PostVoteRepositoryService postVoteRepoService;
    private final PostCommentRepositoryService commentRepoService;
    private final TagRepositoryService tagRepoService;
    private final UserRepositoryService userRepositoryService;
    private final TagToPostRepositoryService tagToPostRepoService;

    @Autowired
    public ApiPostController(PostRepositoryServiceImpl postRepoServiceImpl,
                             PostVoteRepositoryServiceImpl postVoteRepoServiceImpl,
                             PostCommentRepositoryServiceImpl commentRepoServiceImpl,
                             TagRepositoryServiceImpl tagRepoServiceImpl,
                             UserRepositoryServiceImpl userRepoServiceImpl,
                             TagToPostRepositoryServiceImpl tagToPostRepoService) {
        this.postRepoService = postRepoServiceImpl;
        this.postVoteRepoService = postVoteRepoServiceImpl;
        this.commentRepoService = commentRepoServiceImpl;
        this.tagRepoService = tagRepoServiceImpl;
        this.userRepositoryService = userRepoServiceImpl;
        this.tagToPostRepoService = tagToPostRepoService;
    }

    @GetMapping(value = "/api/post", params = {"offset", "limit", "mode"}) // или /posts/? рекомендуется во множественном числе
    public @ResponseBody
    ResponseEntity<String> get(@RequestParam(value = "offset") int offset,  // Может иметь defaultValue = "10"
                       @RequestParam(value = "limit") int limit,
                       @RequestParam(value = "mode") String mode) {
        return postRepoService.getPostsWithParams(offset, limit, mode);
    }

    @GetMapping(value = "/api/post/search", params = {"offset", "query", "limit"})
    public @ResponseBody
    ResponseEntity<String> searchPosts(@RequestParam(value = "offset") int offset,
                       @RequestParam(value = "query") String query,
                       @RequestParam(value = "limit") int limit) {
        return postRepoService.searchPosts(offset, query, limit);
    }

    @GetMapping(value = "/api/post/{id}")
    public @ResponseBody
    ResponseEntity<ResponseAPI> get(@PathVariable int id) {
        return postRepoService.getPost(id);
    }

    @GetMapping(value = "/api/post/byDate", params = {"date", "offset", "limit"})
    public @ResponseBody
    ResponseEntity<String> get(@RequestParam(value = "date") Date date,
                       @RequestParam(value = "offset") int offset,
                       @RequestParam(value = "limit") int limit) {
        return postRepoService.getPostsByDate(date, offset, limit);
    }

    @GetMapping(value = "/api/post/byTag", params = {"tag", "offset", "limit"})
    public @ResponseBody
    ResponseEntity<String> get(@RequestParam(value = "limit") int limit,
                       @RequestParam(value = "tag") String tag,
                       @RequestParam(value = "offset") int offset) {
        return postRepoService.getPostsByTag(limit, tag, offset, tagRepoService);
    }

    @GetMapping(value = "/api/post/moderation", params = {"status", "offset", "limit"})
    public @ResponseBody
    ResponseEntity<String> getPostsForModeration(@RequestParam(value = "status") String status,
                                         @RequestParam(value = "offset") int offset,
                                         @RequestParam(value = "limit") int limit,
                                         HttpServletRequest request) {
        return postRepoService.getPostsForModeration(status, offset, limit, request.getSession(), userRepositoryService);
    }

    @GetMapping(value = "/api/post/my", params = {"status", "offset", "limit"})
    public @ResponseBody
    ResponseEntity<String> getMyPosts(@RequestParam(value = "status") String status,
                              @RequestParam(value = "offset") int offset,
                              @RequestParam(value = "limit") int limit,
                              HttpServletRequest request) {
        return postRepoService.getMyPosts(status, offset, limit, request.getSession(), userRepositoryService);
    }

    @PostMapping(value = "/api/post", params = {"time", "active", "title", "text", "tags"})
    public @ResponseBody
    ResponseEntity<String> post(@RequestParam(value = "time") String time,
                        @RequestParam(value = "active") byte active,
                        @RequestParam(value = "title") String title,
                        @RequestParam(value = "text") String text,
                        @RequestParam(value = "tags") String tags,
                        HttpServletRequest request) throws ParseException {
        return postRepoService.post(time, active, title, text, tags, request.getSession(),
                userRepositoryService, tagRepoService, tagToPostRepoService);
    }

    @PutMapping(value = "/api/post/{id}", params = {"time", "active", "title", "text", "tags"})
    public @ResponseBody
    ResponseEntity<String> editPost(@PathVariable int id,
                            @RequestParam(value = "time") String time,
                            @RequestParam(value = "active") byte active,
                            @RequestParam(value = "title") String title,
                            @RequestParam(value = "text") String text,
                            @RequestParam(value = "tags") String tags,
                            HttpServletRequest request) {
        return postRepoService.editPost(id, time, active, title, text, tags, request.getSession(), userRepositoryService,
                tagRepoService, tagToPostRepoService);
    }

    @PostMapping(value = "/api/post/like", params = {"post_id"})
    public @ResponseBody ResponseEntity<String> likePost(@RequestParam(value = "post_id") int postId, HttpServletRequest request) {
        return postVoteRepoService.likePost(postId, request.getSession(), userRepositoryService, postRepoService);
    }

    @PostMapping(value = "/api/post/dislike", params = {"post_id"})
    public @ResponseBody ResponseEntity<String> dislikePost(@RequestParam(value = "post_id") int postId,
                                                            HttpServletRequest request) {
        return postVoteRepoService.dislikePost(postId, request.getSession(), userRepositoryService, postRepoService);
    }
}
