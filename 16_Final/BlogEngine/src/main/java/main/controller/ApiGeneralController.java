package main.controller;

import main.model.GeneralData;
import main.services.Impl.*;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;

@RestController
@ComponentScan("service")
public class ApiGeneralController {

    private final PostRepositoryService postRepositoryService;
    private final UserRepositoryService userRepositoryService;
    private final GlobalSettingsRepositoryService globalSettingsRepositoryService;
    private final TagRepositoryService tagRepositoryService;
    private final GeneralDataService generalDataService;
    private final PostCommentRepositoryService postCommentRepositoryService;
    private final PostVoteRepositoryService postVoteRepositoryService;

    @Autowired
    public ApiGeneralController (PostRepositoryServiceImpl postRepoServiceImpl,
                                 UserRepositoryServiceImpl userRepoServiceImpl,
                                 GlobalSettingsRepositoryServiceImpl globalSettingsRepoServiceImpl,
                                 TagRepositoryServiceImpl tagRepoServiceImpl,
                                 GeneralDataServiceImpl generalDataServiceImpl,
                                 PostCommentRepositoryServiceImpl postCommentRepoServiceImpl,
                                 PostVoteRepositoryServiceImpl postVoteRepositoryServiceImpl
                                 ) {
        this.postRepositoryService = postRepoServiceImpl;
        this.userRepositoryService = userRepoServiceImpl;
        this.globalSettingsRepositoryService = globalSettingsRepoServiceImpl;
        this.tagRepositoryService = tagRepoServiceImpl;
        this.generalDataService = generalDataServiceImpl;
        this.postCommentRepositoryService = postCommentRepoServiceImpl;
        this.postVoteRepositoryService = postVoteRepositoryServiceImpl;
    }

    @GetMapping(value = "/api/init/")
    public @ResponseBody
    ResponseEntity<GeneralData> getData() {
        return generalDataService.getData();
    }

    @PostMapping(value = "/api/image", params = {"image"})
    public @ResponseBody
    ResponseEntity uploadImage(@RequestParam(value = "image") File image) {
        return postRepositoryService.uploadImage(image); //TODO куда загружать фото? и как?
    }

    @PostMapping(value = "/api/comment/", params = {"parent_id", "post_id", "text"})
    public @ResponseBody
    ResponseEntity<?> addComment(@RequestParam(value = "parent_id") int parentId,
                              @RequestParam(value = "post_id") int postId,
                              @RequestParam(value = "text") String text,
                              HttpServletRequest request
                              ) {
        return postCommentRepositoryService.addComment(parentId, postId, text, request.getSession(),
                userRepositoryService, postRepositoryService);
    }

    @GetMapping(value = "/api/tag/", params = {"query"})
    public @ResponseBody
    ResponseEntity<String> getTags(@RequestParam(value = "query") String query) {
        return tagRepositoryService.getTags(query);
    }

    @PostMapping(value = "/api/moderation", params = {"post_id", "decision"}) // Точно ли ничего не надо возвращать??
    public @ResponseBody
    ResponseEntity<String> moderatePost(@RequestParam(value = "post_id") int postId,
                             @RequestParam(value = "decision") String decision,
                             HttpServletRequest request) {
        return postRepositoryService.moderatePost(postId, decision, request.getSession(), userRepositoryService);
    }

    @GetMapping(value = "/api/calendar/", params = {"year"}) // или years и много лет должно быть???
    public @ResponseBody
    ResponseEntity<String> countPostByYear(@RequestParam(value = "year") Integer year) {
        return postRepositoryService.countPostsByYear(year);
    }

    @PostMapping(value = "/api/profile/my", params = {"photo", "removePhoto", "name", "email", "password"})
    public @ResponseBody ResponseEntity<String> editProfile(@RequestParam(value = "photo") File photo,
                                                    @RequestParam(value = "removePhoto") Byte removePhoto,
                                                    @RequestParam(value = "name") String name,
                                                    @RequestParam(value = "email") String email,
                                                    @RequestParam(value = "password") String password,
                                                    HttpServletRequest request) {
        return userRepositoryService.editProfile(photo, removePhoto, name, email, password, request.getSession());
    }

    @GetMapping(value = "/api/statistics/my")
    public @ResponseBody ResponseEntity<String> getMyStatistics(HttpServletRequest request) {
        return userRepositoryService.getMyStatistics(request.getSession(),
                postVoteRepositoryService, postRepositoryService);
    }

    @GetMapping(value = "/api/statistics/all")
    public @ResponseBody ResponseEntity getAllStatistics(HttpServletRequest request) {
        return userRepositoryService.getAllStatistics(request.getSession(), globalSettingsRepositoryService,
                postVoteRepositoryService, postRepositoryService);
    }

    @GetMapping(value = "/api/settings/")
    public @ResponseBody ResponseEntity<String> getGlobalSettings(HttpServletRequest request) {
        return globalSettingsRepositoryService.getGlobalSettings(request.getSession(), userRepositoryService);
    }

    @PutMapping(value = "/api/settings/", params = {"MULTIUSER_MODE", "POST_PREMODERATION", "STATISTICS_IS_PUBLIC"}) // Как получать параметры и как их устанавливать??
    public @ResponseBody ResponseEntity<String> setGlobalSettings(@RequestParam(value = "MULTIUSER_MODE") Boolean multiUserMode,
                                                          @RequestParam(value = "POST_PREMODERATION") Boolean postPremoderation,
                                                          @RequestParam(value = "STATISTICS_IS_PUBLIC") Boolean statisticsIsPublic,
                                                          HttpServletRequest request) {
        return globalSettingsRepositoryService.setGlobalSettings(multiUserMode, postPremoderation, statisticsIsPublic,
                request.getSession(), userRepositoryService);
    }
}
