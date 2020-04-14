package main.controller;

import lombok.extern.slf4j.Slf4j;
import main.services.interfaces.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Controller
public class FileController {

    @Autowired
    FileSystemService fileSystemService;

    // Аватарки
    @GetMapping({"/**/images/upload/avatars/{imageName}"})
    public ResponseEntity<?> getAvatar(@PathVariable("imageName") String imageName) {
        String pathToFile = "images/upload/avatars/" + imageName;
        System.err.println("ПОЛУЧЕНИ ЗАПРОС pathToFile " + pathToFile);
        return getResponseWithImage(pathToFile);
    }

    // Картинки к постам
    @GetMapping({"/**/images/upload/{subDir1}/{subDir2}/{subDir3}/{imageName}",
            "/**/images/upload/{imageName}"})
    public ResponseEntity<?> getImage(@PathVariable(value = "imageName", required = true) String imageName,
                                      @PathVariable(value = "subDir1", required = false) String subDir1,
                                      @PathVariable(value = "subDir2", required = false) String subDir2,
                                      @PathVariable(value = "subDir3", required = false) String subDir3) {
        String pathToFile = "images/upload/" +
                (subDir1 != null ? subDir1 + "/" : "") +
                (subDir2 != null ? subDir2 + "/" : "") +
                (subDir3 != null ? subDir3 + "/" : "")
                + imageName;
        System.err.println("ПОЛУЧЕНИ ЗАПРОС pathToFile " + pathToFile);
        return getResponseWithImage(pathToFile);
    }

    @GetMapping("/{fileName}") // Изначально только для favicon
    public ResponseEntity<?> getFileFromTemplatesFolder(@PathVariable("fileName") String fileName) {
        String pathName = "src/main/resources/templates/" + fileName;
        System.err.println("ПОЛУЧЕНИ ЗАПРОС pathToFile " + pathName);
        return getResponseWithImage(pathName);
    }

    private ResponseEntity<?> getResponseWithImage(String pathToFile) {
        log.info("--- Запрошен файл изображения: {" + pathToFile + "}");
        try {
            File imageFile = fileSystemService.getFileByPath(pathToFile);
            byte[] image = Files.readAllBytes(imageFile.toPath());
            log.info("--- Получен файл изображения: {" + imageFile.getAbsolutePath() + "}");
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile)))
                    .body(image);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("--- Не удалось получить файл изображения по пути: {" + pathToFile + "}");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

// Временно, пути к изображениям, запрашиваемые с фронта:
//
//            "/post/images/upload/avatars/{imageName}",
//            "/images/upload/avatars/{imageName}",
//            "/my/images/upload/avatars/{imageName}",
//            "/moderation/images/upload/avatars/{imageName}",
//            "/edit/images/upload/avatars/{imageName}"

//            "/post/images/upload/avatars/{imageName}",
//            "/images/upload/avatars/{imageName}",
//            "/my/images/upload/avatars/{imageName}",
//            "/moderation/images/upload/avatars/{imageName}",
//            "/edit/images/upload/avatars/{imageName}"