package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getById(int userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(int userId, UserDto userDto);

    void deleteUser(int userId);
}
