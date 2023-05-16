package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<String, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User getById(int id) {
        return null;
    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void deleteUser() {

    }

    private int getId() {
        int lastId = users.values().stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
