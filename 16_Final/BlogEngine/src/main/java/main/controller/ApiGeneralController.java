package main.controller;

import main.model.GeneralData;
import main.services.Impl.*;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@RestController
@ComponentScan("service")
public class ApiGeneralController {

    private final PostRepositoryService postRepoService;
    private final UserRepositoryService userRepoService;
    private final GlobalSettingsRepositoryService globalSettingsRepoService;
    private final TagRepositoryService tagRepoService;
    private final GeneralDataService generalDataService;
    private final PostCommentRepositoryService commentRepoService;
    private final PostVoteRepositoryService postVoteRepoService;

    @Autowired
    public ApiGeneralController(PostRepositoryServiceImpl postRepoServiceImpl,
                                UserRepositoryServiceImpl userRepoServiceImpl,
                                GlobalSettingsRepositoryServiceImpl globalSettingsRepoServiceImpl,
                                TagRepositoryServiceImpl tagRepoServiceImpl,
                                GeneralDataServiceImpl generalDataServiceImpl,
                                PostCommentRepositoryServiceImpl postCommentRepoServiceImpl,
                                PostVoteRepositoryServiceImpl postVoteRepositoryServiceImpl
    ) {
        this.postRepoService = postRepoServiceImpl;
        this.userRepoService = userRepoServiceImpl;
        this.globalSettingsRepoService = globalSettingsRepoServiceImpl;
        this.tagRepoService = tagRepoServiceImpl;
        this.generalDataService = generalDataServiceImpl;
        this.commentRepoService = postCommentRepoServiceImpl;
        this.postVoteRepoService = postVoteRepositoryServiceImpl;
    }

    @GetMapping(value = "/api/init/")
    public @ResponseBody
    ResponseEntity<GeneralData> getData() {
        return generalDataService.getData();
    }

    @PostMapping(value = "/api/image", params = {"image"})
    public @ResponseBody
    ResponseEntity<String> uploadImage(@RequestParam(value = "image") File image, HttpServletRequest request) {
        return postRepoService.uploadImage(image, request.getSession(), userRepoService); //TODO куда загружать фото? и как?
    }

    @PostMapping(value = "/api/comment/", params = {"parent_id", "post_id", "text"})
    public @ResponseBody
    ResponseEntity<?> addComment(@RequestParam(value = "parent_id") Integer parentId,
                                 @RequestParam(value = "post_id") Integer postId,
                                 @RequestParam(value = "text") String text,
                                 HttpServletRequest request) {
        return commentRepoService.addComment(parentId, postId, text, request.getSession(),
                userRepoService, postRepoService);
    }

    @GetMapping(value = "/api/tag/", params = {"query"})
    public @ResponseBody
    ResponseEntity<String> getTags(@RequestParam(value = "query") String query) {
        return tagRepoService.getTags(query);
    }

    @PostMapping(value = "/api/moderation", params = {"post_id", "decision"}) // Точно ли ничего не надо возвращать??
    public @ResponseBody
    ResponseEntity<String> moderatePost(@RequestParam(value = "post_id") int postId,
                                        @RequestParam(value = "decision") String decision,
                                        HttpServletRequest request) {
        return postRepoService.moderatePost(postId, decision, request.getSession(), userRepoService);
    }

    @GetMapping(value = "/api/calendar/", params = {"year"}) // или years и много лет должно быть???
    public @ResponseBody
    ResponseEntity<String> countPostByYear(@RequestParam(value = "year") Integer year) {
        return postRepoService.countPostsByYear(year);
    }

    @PostMapping(value = "/api/profile/my", params = {"photo", "removePhoto", "name", "email", "password"})
    public @ResponseBody
    ResponseEntity<String> editProfile(@RequestParam(value = "photo") File photo,
                                       @RequestParam(value = "removePhoto") Byte removePhoto,
                                       @RequestParam(value = "name") String name,
                                       @RequestParam(value = "email") String email,
                                       @RequestParam(value = "password") String password,
                                       HttpServletRequest request) {
        return userRepoService.editProfile(photo, removePhoto, name, email, password, request.getSession());
    }

    @GetMapping(value = "/api/statistics/my")
    public @ResponseBody
    ResponseEntity<String> getMyStatistics(HttpServletRequest request) {
        return userRepoService.getMyStatistics(request.getSession());
    }

    @GetMapping(value = "/api/statistics/all")
    public @ResponseBody
    ResponseEntity getAllStatistics(HttpServletRequest request) {
        return userRepoService.getAllStatistics(request.getSession(), globalSettingsRepoService,
                postVoteRepoService, postRepoService);
    }

    @GetMapping(value = "/api/settings/")
    public @ResponseBody
    ResponseEntity<String> getGlobalSettings(HttpServletRequest request) {
        return globalSettingsRepoService.getGlobalSettings(request.getSession(), userRepoService);
    }

    @PutMapping(value = "/api/settings/", params = {"MULTIUSER_MODE", "POST_PREMODERATION", "STATISTICS_IS_PUBLIC"})
    //TODO Как получать параметры Global Settings и как их устанавливать??
    public @ResponseBody
    ResponseEntity<String> setGlobalSettings(@RequestParam(value = "MULTIUSER_MODE") Boolean multiUserMode,
                                             @RequestParam(value = "POST_PREMODERATION") Boolean postPremoderation,
                                             @RequestParam(value = "STATISTICS_IS_PUBLIC") Boolean statisticsIsPublic,
                                             HttpServletRequest request) {
        return globalSettingsRepoService.setGlobalSettings(multiUserMode, postPremoderation, statisticsIsPublic,
                request.getSession(), userRepoService);
    }
}
