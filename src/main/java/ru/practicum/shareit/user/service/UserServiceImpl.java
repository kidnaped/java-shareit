package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.RegistrationException;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.getAllUsers().stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(int userId) {
        return Mapper.toDto(getUserOrThrow(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null) {
            throw new ValidationException("Some required fields are missing!");
        }
        emailValidation(userDto.getEmail());
        User user = Mapper.fromDto(userDto);
        user = repository.saveUser(user);
        return Mapper.toDto(user);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User user = getUserOrThrow(userId);
        User userToCheck;

        if (userDto.getEmail() != null) {
            userToCheck = repository.getByEmail(userDto.getEmail());

            if (userToCheck != null && !userToCheck.equals(user)) {
                throw new RegistrationException("This email is already registered by another user!");
            }
        }

        Mapper.fromDto(userDto, user);
        user = repository.updateUser(user);
        return Mapper.toDto(user);
    }

    @Override
    public void deleteUser(int userId) {
        User user = getUserOrThrow(userId);
        repository.deleteUser(user.getId());
    }

    private void emailValidation(String email) {
        User user = repository.getByEmail(email);
        if (user != null) {
            throw new RegistrationException("User with this email is already exists!");
        }
    }

    private User getUserOrThrow(int id) {
        User user = repository.getById(id);
        if (user == null) {
            throw new NotFoundException("User with tis ID is not found!");
        }
        return user;
    }
}
