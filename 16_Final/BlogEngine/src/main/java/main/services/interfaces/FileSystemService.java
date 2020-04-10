package main.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public interface FileSystemService {

    Boolean uploadFile(File fileToUpload, String fullPathToUpload);

    Boolean deleteFileByPath(String pathToFile);

    File createEmptyFileByPath(String pathToFile);

    Boolean createDirectoriesByPath(String path);

    void copyMultiPartFileToFile(MultipartFile source, File dest);

    void copyMultiPartFileToPath(MultipartFile source, Path dest);

    File getFileByPath(String pathToFile) throws FileNotFoundException;
}
