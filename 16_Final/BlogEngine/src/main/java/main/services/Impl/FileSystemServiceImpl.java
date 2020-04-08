package main.services.Impl;

import lombok.extern.slf4j.Slf4j;
import main.services.interfaces.FileSystemService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public Boolean uploadFile(File srcFile, String destStringPath) {
        if (srcFile == null || destStringPath == null || destStringPath.isBlank()) {
            log.warn("--- Не удалось загрузить файл по пути: " + destStringPath);
            return false;
        }
        log.info("--- Загружается файл: sourceFile:{" +
                "FileName" + srcFile.getName() +
                "} по пути: " + destStringPath);
        File newFile = createEmptyFileByPath(destStringPath);
        if (newFile != null) {          // Значит пустой файл создан
            Path srcPath = srcFile.toPath();
            Path destPath = Paths.get(destStringPath);
            try {
                Files.copy(srcPath, destPath);
                log.info("--- Успешно загружен файл: sourceFile:{" +
                        "FileName" + srcFile.getName() +
                        "} по пути: " + destStringPath);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                log.error("--- Не удалось загрузить файл: sourceFile:{" +
                        "FileName" + srcFile.getName() +
                        "} по пути: " + destStringPath, e);
                return false;
            }
        }
        log.warn("--- Не удалось загрузить файл: sourceFile:{" +
                "FileName" + srcFile.getName() +
                "} по пути: " + destStringPath);
        return false;
    }

    @Override
    public Boolean deleteFileByPath(String pathToFile) {
        log.info("--- Удаляется файл по пути: " + pathToFile);
        if (pathToFile != null && !pathToFile.isBlank()) {
            try {
                boolean isDeleteSucceed = Files.deleteIfExists(Path.of(pathToFile));
                log.info("--- Успшено удален файл по пути: " + pathToFile);
                return isDeleteSucceed;
            } catch (IOException e) {
                e.printStackTrace();
                log.error("--- Не удалось удалить файл по пути: " + pathToFile, e);
                return false;
            }
        }
        log.warn("--- Не удалось удалить файл по пути: " + pathToFile);
        return false;
    }

    @Override
    public File createEmptyFileByPath(String pathToFile) {
        log.info("--- Создается пустой файл по пути: " + pathToFile);
        if (pathToFile != null && !pathToFile.isBlank()) {
            File newFile = null;
            if (!Files.exists(Path.of(pathToFile))) {
                try {
                    Files.createFile(Path.of(pathToFile));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("--- Пустой файл НЕ создан по пути: " + pathToFile, e);
                    return null;
                }
                newFile = new File(pathToFile);
                log.info("--- Пустой файл успешно создан по пути: " + pathToFile);
                return newFile;
            }
        }
        log.warn("--- Пустой файл НЕ создан по пути: " + pathToFile);
        return null;
    }

    @Override
    public Boolean createDirectoriesByPath(String path) {
        log.info("--- Создается папка по пути: " + path);
        try {
            Files.createDirectories(Path.of(path));
            log.info("--- Папка успешно создана по пути: " + path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("--- Папка НЕ создана по пути: " + path, e);
            return false;
        }
    }

    @Override
    public void copyMultiPartFileToFile(MultipartFile source, File dest) {
        log.info("--- Копируются данные файла: sourceFile:{" +
                "FileName:" + source.getOriginalFilename() + "," +
                "FileSize:" + source.getSize() + "}" +
                "в файл: destFile:{" +
                "FileName:" + dest.getName() + "}"
        );
        try {
            byte[] bytes = source.getBytes();
            Files.write(dest.toPath(), bytes);
            log.info("--- Успешно скопированы данные файла: sourceFile:{" +
                    "FileName:" + source.getOriginalFilename() + "," +
                    "FileSize:" + source.getSize() + "}" +
                    "в файл: destFile:{" +
                    "FileName:" + dest.getName() + "}"
            );
        } catch (IOException e) {
            e.printStackTrace();
            log.error("--- Не удалось скопировать данные файла: sourceFile:{" +
                    "FileName:" + source.getOriginalFilename() + "," +
                    "FileSize:" + source.getSize() + "}" +
                    "в файл: destFile:{" +
                    "FileName:" + dest.getName() + "}", e
            );
        }
    }

    @Override
    public void copyMultiPartFileToPath(MultipartFile source, Path dest) {
        log.info("--- Копируются данные файла: sourceFile:{" +
                "FileName:" + source.getOriginalFilename() + "," +
                "FileSize:" + source.getSize() + "}" +
                "в файл: destFile:{" +
                "FileName:" + dest.toString() + "}"
        );
        try {
            source.transferTo(dest);
            log.info("--- Успешно скопированы данные файла: sourceFile:{" +
                    "FileName:" + source.getOriginalFilename() + "," +
                    "FileSize:" + source.getSize() + "}" +
                    "в файл: destFile:{" +
                    "FileName:" + dest.toString() + "}"
            );
        } catch (IOException e) {
            e.printStackTrace();
            log.error("--- Не удалось скопировать данные файла: sourceFile:{" +
                    "FileName:" + source.getOriginalFilename() + "," +
                    "FileSize:" + source.getSize() + "}" +
                    "в файл: destFile:{" +
                    "FileName:" + dest.toString() + "}", e
            );
        }
    }

    @Override
    public File getFileByPath(String pathToFile) {
        File file = new File(pathToFile);
        log.info("--- Получен файл по пути: {" +
                pathToFile + "}"
        );
        return file;
    }
}
