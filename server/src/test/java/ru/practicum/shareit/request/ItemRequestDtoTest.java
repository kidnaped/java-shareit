package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> requestDtoJacksonTester;
    @Autowired
    private JacksonTester<ItemRequestCreationDto> requestCreationDtoJacksonTester;

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
    void itemRequestDtoSerializationTest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Iron")
                .requester(user)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
        JsonContent<ItemRequestDto> itemRequestDtoJsonContent = requestDtoJacksonTester.write(itemRequestDto);

        assertThat(itemRequestDtoJsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(itemRequest.getId()));
        assertThat(itemRequestDtoJsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDtoJsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequest.getCreated().truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    @SneakyThrows
    void itemRequestInputDtoDeserializationTest() {
        ItemRequestCreationDto itemRequestInputDto = new ItemRequestCreationDto("NotIron");

        JsonContent<ItemRequestCreationDto> itemRequestInputDtoJsonContent = requestCreationDtoJacksonTester
                .write(itemRequestInputDto);
        ItemRequest newItemRequest = ItemRequestMapper.fromDto(requestCreationDtoJacksonTester
                .parseObject(itemRequestInputDtoJsonContent.getJson()), user);

        assertThat(newItemRequest).hasFieldOrPropertyWithValue("description",
                itemRequestInputDto.getDescription());
    }
}