package com.zagbor.wallet.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zagbor.wallet.model.Category;
import com.zagbor.wallet.model.Transaction;
import com.zagbor.wallet.model.TransactionType;
import com.zagbor.wallet.model.User;
import com.zagbor.wallet.model.Wallet;
import com.zagbor.wallet.service.FileService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class Storage {

    private Map<String, User> userMap;

    private final ObjectMapper objectMapper;

    @Bean
    public Map<String, User> userMap() {
        return userMap;
    }

    // Сохранение пользователей в файл
    @PreDestroy
    public boolean saveUsersToFile() {
        String filePath = "users.json";
        try {
            // Очистка файла перед сохранением
            new FileWriter(filePath, false).close();

            objectMapper.writeValue(new File(filePath), userMap.values()); // Сериализация в JSON
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostConstruct
    public boolean loadUsersFromFile() {
        String filePath = "users.json";
        // Проверяем существование файла, если нет — создаём
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false; // Возвращаем false, так как файл не существует
        }

        // Проверяем, что файл не пустой
        if (file.length() == 0) {
            System.out.println("Файл пуст, пропускаем загрузку.");
            return false; // Если файл пустой, пропускаем метод
        }

        // Читаем содержимое файла и десериализуем в массив пользователей
        try {
            User[] users = objectMapper.readValue(file, User[].class); // Десериализация из JSON
            for (User user : users) {
                userMap.put(user.getUsername(), user);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Возвращаем false в случае ошибки при чтении файла
        }
    }

    private User parseUser(String userString) {
        try {
            return objectMapper.readValue(userString, User.class); // Преобразование строки в объект
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Добавление пользователя в Map
    public void addUser(User user) {
        userMap.put(user.getUsername(), user);
    }

    // Получение пользователя по имени
    public User getUserByUsername(String username) {
        return userMap.get(username);
    }

    // Удаление пользователя
    public boolean removeUser(String username) {
        if (userMap.containsKey(username)) {
            userMap.remove(username);
            return true;
        }
        return false;
    }
}
