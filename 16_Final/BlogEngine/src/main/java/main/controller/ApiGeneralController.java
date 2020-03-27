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

    @Autowired
    private PostRepositoryService postRepoService;
    @Autowired
    private UserRepositoryService userRepoService;
    @Autowired
    private GlobalSettingsRepositoryService globalSettingsRepoService;
    @Autowired
    private TagRepositoryService tagRepoService;
    @Autowired
    private GeneralDataService generalDataService;
    @Autowired
    private PostCommentRepositoryService commentRepoService;

    public ApiGeneralController() {
    }

    @GetMapping(value = "/api/init")
    public ResponseEntity<ResponseApi> getData() {
        return generalDataService.getData();
    }

    @PostMapping(value = "/api/image")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile image,
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
    public ResponseEntity<ResponseApi> addComment(@RequestBody AddCommentRequest addCommentRequest,
                                                  HttpServletRequest request) {
        return commentRepoService.addComment(addCommentRequest, request.getSession());
    }

    @GetMapping(value = "/api/tag", params = {"query"})
    public ResponseEntity<ResponseApi> getTags(@RequestParam(value = "query") String query) {
        return tagRepoService.getTags(query);
    }

    @GetMapping(value = "/api/tag")
    public ResponseEntity<ResponseApi> getTagsWithoutQuery() {
        return tagRepoService.getTagsWithoutQuery();
    }

    @PostMapping(value = "/api/moderation")
    public ResponseEntity<ResponseApi> moderatePost(@RequestBody ModeratePostRequest moderatePostRequest,
                                                    HttpServletRequest request) {
        return postRepoService.moderatePost(moderatePostRequest, request.getSession());
    }

    @GetMapping(value = "/api/calendar", params = {"year"})
    public ResponseEntity<ResponseApi> countPostByYear(@RequestParam(value = "year") Integer year) {
        return postRepoService.countPostsByYear(year);
    }

    @PostMapping(value = "/api/profile/my")
    public ResponseEntity<ResponseApi> editProfile(@RequestBody EditProfileRequest editProfileRequest,
                                                   HttpServletRequest request, @RequestParam("photo") MultipartFile image) {
        if (image != null) {
            try {
                userRepoService.uploadImage(image, request.getSession());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userRepoService.editProfile(editProfileRequest, request.getSession());
    }

    @GetMapping(value = "/api/statistics/my")
    public ResponseEntity<?> getMyStatistics(HttpServletRequest request) {
        return userRepoService.getMyStatistics(request.getSession());
    }

    @GetMapping(value = "/api/statistics/all")
    public ResponseEntity<?> getAllStatistics(HttpServletRequest request) {
        return userRepoService.getAllStatistics(request.getSession());
    }

    @GetMapping(value = "/api/settings")
    public ResponseEntity<?> getGlobalSettings(HttpServletRequest request) {
        return globalSettingsRepoService.getGlobalSettings(request.getSession());
    }

    @PutMapping(value = "/api/settings")
    //TODO как их устанавливать??
    public ResponseEntity<?> setGlobalSettings(@RequestBody SetGlobalSettingsRequest setGlobalSettingsRequest,
                                               HttpServletRequest request) {
        return globalSettingsRepoService.setGlobalSettings(setGlobalSettingsRequest,
                request.getSession());
    }
}
