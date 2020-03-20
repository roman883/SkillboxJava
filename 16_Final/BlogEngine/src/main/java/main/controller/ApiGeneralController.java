package main.controller;

import main.api.request.AddCommentRequest;
import main.api.request.EditProfileRequest;
import main.api.request.ModeratePostRequest;
import main.api.request.SetGlobalSettingsRequest;
import main.api.response.ResponseApi;
import main.services.Impl.*;
import main.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@ComponentScan("service")
public class ApiGeneralController {

    private final PostRepositoryService postRepoService;
    private final UserRepositoryService userRepoService;
    private final GlobalSettingsRepositoryService globalSettingsRepoService;
    private final TagRepositoryService tagRepoService;
    private final GeneralDataService generalDataService;
    private final PostCommentRepositoryService commentRepoService;

    @Autowired
    public ApiGeneralController(PostRepositoryServiceImpl postRepoServiceImpl,
                                UserRepositoryServiceImpl userRepoServiceImpl,
                                GlobalSettingsRepositoryServiceImpl globalSettingsRepoServiceImpl,
                                TagRepositoryServiceImpl tagRepoServiceImpl,
                                GeneralDataServiceImpl generalDataServiceImpl,
                                PostCommentRepositoryServiceImpl postCommentRepoServiceImpl
    ) {
        this.postRepoService = postRepoServiceImpl;
        this.userRepoService = userRepoServiceImpl;
        this.globalSettingsRepoService = globalSettingsRepoServiceImpl;
        this.tagRepoService = tagRepoServiceImpl;
        this.generalDataService = generalDataServiceImpl;
        this.commentRepoService = postCommentRepoServiceImpl;
    }

    @GetMapping(value = "/api/init")
    public @ResponseBody
    ResponseEntity<ResponseApi> getData() {
        return generalDataService.getData();
    }

    @PostMapping(value = "/api/image")
    public @ResponseBody
    ResponseEntity<String> uploadImage(@RequestParam MultipartFile image,
                                       HttpServletRequest request) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = postRepoService.uploadImage(image, request.getSession()); //TODO куда загружать фото? ОГРАНИЧЕНИЕ РАЗМЕРА ФАЙЛА 1 МБ стандартно
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseEntity;
    }

    @PostMapping(value = "/api/comment")
    public @ResponseBody
    ResponseEntity<ResponseApi> addComment(@RequestBody AddCommentRequest addCommentRequest,
                                           HttpServletRequest request) {
        return commentRepoService.addComment(addCommentRequest, request.getSession());
    }

    @GetMapping(value = "/api/tag", params = {"query"})
    public @ResponseBody
    ResponseEntity<ResponseApi> getTags(@RequestParam(value = "query") String query) {
        return tagRepoService.getTags(query);
    }

    @GetMapping(value = "/api/tag")
    public @ResponseBody
    ResponseEntity<ResponseApi> getTagsWithoutQuery() {
        return tagRepoService.getTagsWithoutQuery();
    }

    @PostMapping(value = "/api/moderation") // Точно ли ничего не надо возвращать??
    public @ResponseBody
    ResponseEntity<ResponseApi> moderatePost(@RequestBody ModeratePostRequest moderatePostRequest,
                                             HttpServletRequest request) {
        return postRepoService.moderatePost(moderatePostRequest, request.getSession());
    }

    @GetMapping(value = "/api/calendar", params = {"year"}) // или years и много лет должно быть???
    public @ResponseBody
    ResponseEntity<ResponseApi> countPostByYear(@RequestParam(value = "year") Integer year) {
        return postRepoService.countPostsByYear(year);
    }

    @PostMapping(value = "/api/profile/my") // params = {"photo", "removePhoto", "name", "email", "password"}
    public @ResponseBody
    ResponseEntity<ResponseApi> editProfile(@RequestBody EditProfileRequest editProfileRequest,
                                            HttpServletRequest request) {
        return userRepoService.editProfile(editProfileRequest, request.getSession());
    }

    @GetMapping(value = "/api/statistics/my")
    public @ResponseBody
    ResponseEntity<?> getMyStatistics(HttpServletRequest request) {
        return userRepoService.getMyStatistics(request.getSession());
    }

    @GetMapping(value = "/api/statistics/all")
    public @ResponseBody
    ResponseEntity<?> getAllStatistics(HttpServletRequest request) {
        return userRepoService.getAllStatistics(request.getSession());
    }

    @GetMapping(value = "/api/settings")
    public @ResponseBody
    ResponseEntity<?> getGlobalSettings(HttpServletRequest request) {
        return globalSettingsRepoService.getGlobalSettings(request.getSession());
    }

    @PutMapping(value = "/api/settings")
    //TODO Как получать параметры Global Settings и как их устанавливать??
    public @ResponseBody
    ResponseEntity<?> setGlobalSettings(@RequestBody SetGlobalSettingsRequest setGlobalSettingsRequest,
                                        HttpServletRequest request) {
        return globalSettingsRepoService.setGlobalSettings(setGlobalSettingsRequest,
                request.getSession());
    }
}
