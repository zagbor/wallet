package com.zagbor.wallet.service;

import com.zagbor.wallet.model.User;
import com.zagbor.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("Invalid username: '" + username + "'")
        );
    }

    public boolean updateUser(String username, User user) {
        userRepository.updateUser(username, user);
        logger.info("User with username '{}' has been updated. New details: Name = '{}'",
                username, username);
        return true;
    }

    public boolean checkUserExists(String username) {
        return userRepository.getUserByUsername(username).isPresent();
    }


}
