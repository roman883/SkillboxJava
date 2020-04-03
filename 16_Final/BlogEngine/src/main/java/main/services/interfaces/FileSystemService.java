package main.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileSystemService {

    Boolean uploadFile(File fileToUpload, String fullPathToUpload);

    Boolean deleteFileByPath(String pathToFile);

    File createEmptyFileByPath(String pathToFile);

    Boolean createDirectoriesByPath(String path);

    void copyMultiPartFileToFile(MultipartFile source, File dest);
}
