package main.controller;

import main.services.Impl.PostRepositoryServiceImpl;
import main.services.Impl.PostVoteRepositoryServiceImpl;
import main.services.interfaces.PostRepositoryService;
import main.services.interfaces.PostVoteRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;

@RestController
@ComponentScan("service")
public class ApiPostController {

    private final PostRepositoryService postRepositoryService;
    private final PostVoteRepositoryService postVoteRepositoryService;

    @Autowired
    public ApiPostController(PostRepositoryServiceImpl postRepositoryServiceImpl,
                             PostVoteRepositoryServiceImpl postVoteRepositoryServiceImpl) {
        this.postRepositoryService = postRepositoryServiceImpl;
        this.postVoteRepositoryService = postVoteRepositoryServiceImpl;
    }

    @GetMapping(value = "/api/post/", params = {"offset", "limit", "mode"})
    // или /posts/? рекомендуется во множественном числе
    public @ResponseBody
    ResponseEntity get(@RequestParam(value = "offset") int offset,  // Может иметь defaultValue = "10"
                       @RequestParam(value = "limit") int limit,
                       @RequestParam(value = "mode") String mode) {
        return postRepositoryService.getPostsWithParams(offset, limit, mode);
    }

    @GetMapping(value = "/api/post/search/", params = {"offset", "query", "limit"})
    public @ResponseBody
    ResponseEntity searchPosts(@RequestParam(value = "offset") int offset,
                       @RequestParam(value = "query") String query,
                       @RequestParam(value = "limit") int limit) {
        return postRepositoryService.searchPosts(offset, query, limit);
    }

    @GetMapping(value = "/api/post/{id}")
    public @ResponseBody
    ResponseEntity get(@PathVariable int id) {
        return postRepositoryService.getPost(id);
    }

    @GetMapping(value = "/api/post/byDate", params = {"date", "offset", "limit"})
    public @ResponseBody
    ResponseEntity get(@RequestParam(value = "date") Date date,
                       @RequestParam(value = "offset") int offset,
                       @RequestParam(value = "limit") int limit) {
        return postRepositoryService.getPostsByDate(date, offset, limit);
    }

    @GetMapping(value = "/api/post/byTag", params = {"tag", "offset", "limit"})
    public @ResponseBody
    ResponseEntity get(@RequestParam(value = "limit") int limit,
                       @RequestParam(value = "tag") String tag,
                       @RequestParam(value = "offset") int offset) {
        return postRepositoryService.getPostsByTag(limit, tag, offset);
    }

    @GetMapping(value = "/api/post/moderation", params = {"status", "offset", "limit"})
    public @ResponseBody
    ResponseEntity getPostsForModeration(@RequestParam(value = "status") String status,
                                         @RequestParam(value = "offset") int offset,
                                         @RequestParam(value = "limit") int limit) {
        return postRepositoryService.getPostsForModeration(status, offset, limit);
    }

    @GetMapping(value = "/api/post/my", params = {"status", "offset", "limit"})
    public @ResponseBody
    ResponseEntity getMyPosts(@RequestParam(value = "status") String status,
                              @RequestParam(value = "offset") int offset,
                              @RequestParam(value = "limit") int limit) {
        return postRepositoryService.getMyPosts(status, offset, limit);
    }

    @PostMapping(value = "/api/post", params = {"time", "active", "title", "text", "tags"})
    public @ResponseBody
    ResponseEntity post(@RequestParam(value = "time") Time time,
                        @RequestParam(value = "active") byte active,
                        @RequestParam(value = "title") String title,
                        @RequestParam(value = "text") String text,
                        @RequestParam(value = "tags") String tags
    ) {
        return postRepositoryService.post(time, active, title, text, tags);
    }

    @PutMapping(value = "/api/post/{id}", params = {"time", "active", "title", "text", "tags"})
    public @ResponseBody
    ResponseEntity editPost(@PathVariable int id,
                            @RequestParam(value = "time") Time time,
                            @RequestParam(value = "active") byte active,
                            @RequestParam(value = "title") String title,
                            @RequestParam(value = "text") String text,
                            @RequestParam(value = "tags") String tags
    ) {
        return postRepositoryService.editPost(id, time, active, title, text, tags);
    }

    @PostMapping(value = "/api/post/like", params = {"post_id"})
    public @ResponseBody ResponseEntity likePost(@RequestParam(value = "post_id") int postId) {
        return postVoteRepositoryService.likePost(postId);
    }

    @PostMapping(value = "/api/post/dislike", params = {"post_id"})
    public @ResponseBody ResponseEntity dislikePost(@RequestParam(value = "post_id") int postId) {
        return postVoteRepositoryService.dislikePost(postId);
    }
}
