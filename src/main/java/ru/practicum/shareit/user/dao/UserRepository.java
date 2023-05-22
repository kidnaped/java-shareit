package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getById(int id);

    User getByEmail(String email);

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(int id);
}
