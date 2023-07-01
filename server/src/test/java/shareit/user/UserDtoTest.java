package shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;
    @Autowired
    private JacksonTester<UserShortDto> userShortDtoJacksonTester;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@ya.ru")
                .build();
    }

    @Test
    @SneakyThrows
    void userFullDtoSerializationTest() {
        UserDto userFullDto = UserMapper.toDto(user);
        JsonContent<UserDto> userFullDtoJsonContent = userDtoJacksonTester.write(userFullDto);

        assertThat(userFullDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(user.getId()));
        assertThat(userFullDtoJsonContent)
                .extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
        assertThat(userFullDtoJsonContent)
                .extractingJsonPathStringValue("$.email").isEqualTo(user.getEmail());
    }

    @Test
    @SneakyThrows
    void userShortDtoSerializationTest() {
        UserShortDto userShortDto = UserMapper.toShortDto(user);
        JsonContent<UserShortDto> userShortDtoJsonContent = userShortDtoJacksonTester.write(userShortDto);

        assertThat(userShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(user.getId()));
        assertThat(userShortDtoJsonContent)
                .extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
    }
}