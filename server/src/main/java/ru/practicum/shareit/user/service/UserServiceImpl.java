package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        log.info("Found all users.");
        return UserMapper.toDto(users);
    }

    @Override
    public UserDto getById(long userId) {
        User user = getUserById(userId);
        log.info("User {} found.", user.getName());
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null) {
            throw new ValidationException("Some required fields are missing!");
        }
        User user = UserMapper.fromDto(userDto);
        user = repository.save(user);
        log.info("User {}, {} registered.", user.getId(), user.getName());
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = getUserById(userId);
        UserMapper.fromDto(userDto, user);
        user = repository.save(user);
        log.info("User {}, {} updated.", user.getId(), user.getName());
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        repository.deleteById(user.getId());
        log.info("User {} deleted.", user.getName());
    }

    @Override
    public User getUserById(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with provided ID is not found!"));
    }
}
