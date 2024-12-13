package com.zagbor.wallet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    // Сохранение текста в файл
    public boolean saveTextToFile(String content, String filePath) {
        logger.info("Saving text to file: {}", filePath);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
            logger.info("Content successfully saved to file: {}", filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error saving content to file: {}", filePath, e);
            return false;
        }
    }

    // Чтение текста из файла
    public String readTextFromFile(String filePath) {
        logger.info("Reading content from file: {}", filePath);
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            logger.info("Content successfully read from file: {}", filePath);
            return content;
        } catch (IOException e) {
            logger.error("Error reading content from file: {}", filePath, e);
            return null;
        }
    }

    // Загрузка файла в систему (например, для обработки данных)
    public boolean uploadFile(File file, String targetDirectory) {
        logger.info("Uploading file: {} to directory: {}", file.getName(), targetDirectory);
        try {
            Path targetPath = Paths.get(targetDirectory, file.getName());
            Files.copy(file.toPath(), targetPath);
            logger.info("File uploaded successfully to: {}", targetPath.toString());
            return true;
        } catch (IOException e) {
            logger.error("Error uploading file: {}", file.getName(), e);
            return false;
        }
    }

    // Удаление файла
    public boolean deleteFile(String filePath) {
        logger.info("Deleting file: {}", filePath);
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            logger.info("File deleted successfully: {}", filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error deleting file: {}", filePath, e);
            return false;
        }
    }

    // Чтение строк из файла (например, CSV или текстового файла)
    public List<String> readLinesFromFile(String filePath) {
        logger.info("Reading lines from file: {}", filePath);
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            logger.info("Lines successfully read from file: {}", filePath);
            return lines;
        } catch (IOException e) {
            logger.error("Error reading lines from file: {}", filePath, e);
            return null;
        }
    }

    // Печать содержимого файла
    public void printFileContent(String filePath) {
        String content = readTextFromFile(filePath);
        if (content != null) {
            System.out.println(content);
        } else {
            logger.warn("No content found in file: {}", filePath);
        }
    }

    // Проверка существования файла
    public boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }

    public void createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.createNewFile()) {
                System.out.println("File created: " + filePath);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating file: " + filePath, e);
        }
    }
}
