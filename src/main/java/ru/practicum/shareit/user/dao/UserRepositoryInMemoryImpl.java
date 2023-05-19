package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int globalId = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    @Override
    public User getByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElse(null);
    }

    @Override
    public User saveUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    private int getId() {
        return ++globalId;
    }
}
