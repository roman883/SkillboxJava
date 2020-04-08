package main.controller;

import lombok.extern.slf4j.Slf4j;
import main.services.interfaces.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Controller
public class DefaultController {
    @Value("${user.image.root_folder}")
    private String rootPathToUploadAvatars;
    @Value("${user.image.upload_folder}")
    private String uploadFolder;
    @Value("${user.image.avatars_folder}")
    private String avatarsFolder;

    @Autowired
    FileSystemService fileSystemService;

    @RequestMapping("/")
    public String index(Model model) {
        log.info("--- Получен запрос на \"/\" Вернули index");
        return "index";
    }

    @RequestMapping(method =
            {RequestMethod.OPTIONS, RequestMethod.GET}, //принимаем только GET OPTIONS
            value = "/**/{path:[^\\.]*}") //описание обрабатываемых ссылок (регулярка с переменной)
    public String redirectToIndex() {
        log.info("--- Получен запрос на адрес соответствующий /**/{path:[^\\.]*} Выполнили перенаправление forward:/");
        return "forward:/"; //делаем перенаправление
    }

    // Можно ли в RequestMapping("/КАКОЙ-то ПУТЬ с переменными из YML/{path_variable}") использовать частично данный из yml а частично pathVariable
    @GetMapping("/posts/images/upload/avatars/{imageName}")
    public ResponseEntity<?> getAvatarImagePostsPath(@PathVariable("imageName") String imageName) {
        log.info("--- Запрошен файл изображения: {" + imageName + "}");
//        StringBuilder builder = new StringBuilder(rootPathToUploadAvatars).append("/")
//                .append(uploadFolder).append("/").append(avatarsFolder);
//        String pathToFileFolder = builder.toString();
        String pathToFile = "images/upload/avatars/" + imageName;
//        if (imageName.startsWith(pathToFileFolder)) {
//            pathToFile = "/images/upload/avatars/" + imageName;
//        } else return ResponseEntity.badRequest().body("Файл по указанному пути отсутствует");
        try {
            File imageFile = fileSystemService.getFileByPath(pathToFile);
            byte[] image = Files.readAllBytes(imageFile.toPath());
            log.info("--- Получен файл изображения: {" + imageFile.getAbsolutePath() + "}");
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile)))
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("--- Не удалось получить файл изображения по пути: {" + pathToFile + "}");
            return ResponseEntity.badRequest().body("Файл по указанному пути отсутствует");
        }
    }

    @GetMapping("/images/upload/avatars/{imageName}")
    public ResponseEntity<?> getAvatarImage(@PathVariable("imageName") String imageName) {
        log.info("--- Запрошен файл изображения: {" + imageName + "}");
        String pathToFile = "images/upload/avatars/" + imageName;
        try {
            File imageFile = fileSystemService.getFileByPath(pathToFile);
            byte[] image = Files.readAllBytes(imageFile.toPath());
            log.info("--- Получен файл изображения: {" + imageFile.getAbsolutePath() + "}");
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile)))
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("--- Не удалось получить файл изображения по пути: {" + pathToFile + "}");
            return ResponseEntity.badRequest().body("Файл по указанному пути отсутствует");
        }
    }

    @GetMapping("/my/images/upload/avatars/{imageName}")
    public ResponseEntity<?> getMyAvatarImage(@PathVariable("imageName") String imageName) {
        log.info("--- Запрошен файл изображения: {" + imageName + "}");
        String pathToFile = "images/upload/avatars/" + imageName;
        try {
            File imageFile = fileSystemService.getFileByPath(pathToFile);
            byte[] image = Files.readAllBytes(imageFile.toPath());
            log.info("--- Получен файл изображения: {" + imageFile.getAbsolutePath() + "}");
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile)))
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("--- Не удалось получить файл изображения по пути: {" + pathToFile + "}");
            return ResponseEntity.badRequest().body("Файл по указанному пути отсутствует");
        }
    }

    @GetMapping("/images/upload/{imageName}")
    public ResponseEntity<?> getImage(@PathVariable("imageName") String imageName) {
        log.info("--- Запрошен файл изображения: {" + imageName + "}");
        String pathToFile = "images/upload/" + imageName;
        try {
            File imageFile = fileSystemService.getFileByPath(pathToFile);
            byte[] image = Files.readAllBytes(imageFile.toPath());
            log.info("--- Получен файл изображения: {" + imageFile.getAbsolutePath() + "}");
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile)))
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("--- Не удалось получить файл изображения по пути: {" + pathToFile + "}");
            return ResponseEntity.badRequest().body("Файл по указанному пути отсутствует");
        }
    }

    @GetMapping("/post/images/upload/{imageName}")
    public ResponseEntity<?> getImageByPostsPath(@PathVariable("imageName") String imageName) {
        log.info("--- Запрошен файл изображения: {" + imageName + "}");
        String pathToFile = "images/upload/" + imageName;
        try {
            File imageFile = fileSystemService.getFileByPath(pathToFile);
            byte[] image = Files.readAllBytes(imageFile.toPath());
            log.info("--- Получен файл изображения: {" + imageFile.getAbsolutePath() + "}");
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile)))
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("--- Не удалось получить файл изображения по пути: {" + pathToFile + "}");
            return ResponseEntity.badRequest().body("Файл по указанному пути отсутствует");
        }
    }
}