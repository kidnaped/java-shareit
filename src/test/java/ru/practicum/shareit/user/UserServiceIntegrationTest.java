package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    void shouldReturnListOfUsersWhenGettingAllUsersAfterCreation() {
        UserDto userCreationDto1 = UserDto.builder()
                .name("UserDto")
                .email("userDto@ya.ru")
                .build();
        UserDto userCreationDto2 = UserDto.builder()
                .name("UserDto2")
                .email("userDto2@ya.ru")
                .build();
        UserDto userDto1 = userService.createUser(userCreationDto1);
        UserDto userDto2 = userService.createUser(userCreationDto2);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users.size()).isEqualTo(2);

        assertThat(users.get(0)).isEqualTo(userDto1);

        assertThat(users.get(1)).isEqualTo(userDto2);
    }
}