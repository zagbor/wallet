package com.zagbor.wallet.storage;

import com.zagbor.wallet.manager.FileManager;
import com.zagbor.wallet.model.Category;
import com.zagbor.wallet.model.Transaction;
import com.zagbor.wallet.model.TransactionType;
import com.zagbor.wallet.model.User;
import com.zagbor.wallet.model.Wallet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
public class Storage {

    private final FileManager fileManager;

    // Хранение пользователей в Map
    private Map<String, User> userMap;

    @Autowired
    public Storage(FileManager fileManager) {
        this.fileManager = fileManager;
        this.userMap = new HashMap<>();
    }

    @Bean
    public Map<String, User> userMap() {
        Wallet wallet = new Wallet(1L, new BigDecimal(15000), new ArrayList<>(),
                new ArrayList<>());
        Category category = new Category(1L, "food", new BigDecimal(10000), new BigDecimal(0), wallet);
        Transaction transaction = new Transaction(1L, "banana", TransactionType.EXPENSE, new BigDecimal(500), LocalDate.now(), category, wallet);
        wallet.budgetCategories().add(category);
        wallet.transactions().add(transaction);
        userMap.put("anonymousUser",
                new User(1L, "anonymousUser", wallet));
        return userMap;
    }

    // Сохранение пользователей в файл
    public boolean saveUsersToFile(String filePath) {
        StringBuilder content = new StringBuilder();
        for (User user : userMap.values()) {
            content.append(user.toString())
                    .append("\n"); // Преобразование пользователя в строку (сначала нужно реализовать метод toString)
        }
        return fileManager.saveTextToFile(content.toString(), filePath);
    }

    // Загрузка пользователей из файла
    public boolean loadUsersFromFile(String filePath) {
        String content = fileManager.readTextFromFile(filePath);
        if (content == null || content.isEmpty()) {
            return false; // Файл пуст или ошибка чтения
        }

        String[] userStrings = content.split("\n");
        for (String userString : userStrings) {
            User user = parseUser(userString); // Метод для парсинга строки в объект User
            if (user != null) {
                userMap.put(user.username(), user);
            }
        }
        return true;
    }

    // Преобразование строки в объект User
    private User parseUser(String userString) {
        try {
            // Логика для преобразования строки в объект User
            // Например, разделение строки на части и создание пользователя
            String[] userFields = userString.split(",");

            if (userFields.length < 3) {
                return null; // Если строка не содержит всех полей, возвращаем null
            }

            Long id = Long.parseLong(userFields[0].trim());  // Преобразуем id в Long
            String username = userFields[1].trim();  // Получаем username

            // Для wallet можно добавить логику для создания объекта Wallet из строки.
            // Допустим, мы парсим ID кошелька, баланс и список транзакций (простой пример):
            Wallet wallet = parseWallet(userFields[3].trim());  // Предположим, что wallet в строке

            return new User(id, username, wallet);
        } catch (Exception e) {
            return null; // Возвращаем null в случае ошибки парсинга
        }
    }

    private Wallet parseWallet(String walletString) {
        try {
            // Логика для парсинга строки в объект Wallet
            // Здесь можно добавить логику для преобразования строки в объект Wallet
            // Пример: "12345,1000.50" -> id=12345, balance=1000.50
            String[] walletFields = walletString.split(",");
            Long walletId = Long.parseLong(walletFields[0].trim());
            BigDecimal balance = new BigDecimal(walletFields[1].trim());

            // Пример, если в Wallet есть категории и транзакции, их можно парсить аналогично
            // В реальном коде, вероятно, понадобятся дополнительные данные и парсинг для транзакций и категорий

            return new Wallet(walletId, balance, new ArrayList<>(), new ArrayList<>()); // Пример для Wallet
        } catch (Exception e) {
            return null; // Возвращаем null, если возникла ошибка парсинга
        }
    }

    // Добавление пользователя в Map
    public void addUser(User user) {
        userMap.put(user.username(), user);
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
