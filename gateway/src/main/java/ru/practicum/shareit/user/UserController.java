package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserInputDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserInputDto dto) {
        log.info("Create user: name = {}, email = {}", dto.getName(), dto.getEmail());
        return client.createUser(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserInputDto dto) {
        log.info("Update user {}.", userId);
        return client.updateUser(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users.");
        return client.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Getting user {}.", userId);
        return client.getByUserId(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable Long userId) {
        log.info("Deleting user {}.", userId);
        return client.deleteUser(userId);
    }
}
