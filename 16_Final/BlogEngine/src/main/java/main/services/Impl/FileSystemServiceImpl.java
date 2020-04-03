package main.services.Impl;

import main.services.interfaces.FileSystemService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public Boolean uploadFile(File srcFile, String destStringPath) {
        if (srcFile == null || destStringPath == null || destStringPath.isBlank()) return false;
        File newFile = createEmptyFileByPath(destStringPath);
        if (newFile != null) {          // Значит пустой файл создан
            Path srcPath = srcFile.toPath();
            Path destPath = Paths.get(destStringPath);
            try {
                Files.copy(srcPath, destPath);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
//                try (InputStream inputStream = new FileInputStream(srcFile);
//                     OutputStream outputStream = new FileOutputStream(newFile)) { // Autocloseable потоки
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = inputStream.read(buffer)) > 0) {
//                        outputStream.write(buffer, 0, length);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
        }
        return false;
    }

    @Override
    public Boolean deleteFileByPath(String pathToFile) {
        if (pathToFile != null && !pathToFile.isBlank()) {
            try {
                return Files.deleteIfExists(Path.of(pathToFile));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public File createEmptyFileByPath(String pathToFile) {
        if (pathToFile != null && !pathToFile.isBlank()) {
            File newFile = null;
            if (!Files.exists(Path.of(pathToFile))) {
                try {
                    Files.createFile(Path.of(pathToFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                newFile = new File(pathToFile);
            }
        }
        return null;
    }

    @Override
    public Boolean createDirectoriesByPath(String path) {
        try {
            Files.createDirectories(Path.of(path));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void copyMultiPartFileToFile(MultipartFile source, File dest) {
        try {
            byte[] bytes = source.getBytes();
            Files.write(dest.toPath(), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
