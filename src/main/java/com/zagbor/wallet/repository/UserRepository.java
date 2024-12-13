package com.zagbor.wallet.repository;

import com.zagbor.wallet.model.User;
import com.zagbor.wallet.service.UserService;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final Map<String, User> userMap; // Хранилище пользователей

    public User createUser(User user) {
        if (userMap.containsKey(user.getUsername())) {
            logger.error("User creation failed: User with username '{}' already exists", user.getUsername());
        }
        userMap.put(user.getUsername(), user);
        logger.info("User with username '{}' successfully created", user.getUsername());
        return user;
    }

    // Получение пользователя по имени
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userMap.get(username));
    }


    public boolean updateUser(String username, User updatedUser) {
        if (!userMap.containsKey(username)) {
            logger.warn("Failed to update user: User with username '{}' not found", username);
            return false;
        }
        userMap.put(username, updatedUser);
        logger.info("User with username '{}' successfully updated", username);
        return true;
    }

    public boolean removeUser(String username) {
        if (userMap.containsKey(username)) {
            userMap.remove(username); // Удаляем пользователя из хранилища
            return true;
        }
        return false; // Если пользователя с таким именем не существует
    }
}
