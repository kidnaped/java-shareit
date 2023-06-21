package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> itemFullDtoJacksonTester;
    @Autowired
    private JacksonTester<ItemShortDto> itemShortDtoJacksonTester;

    private Item item;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(2L)
                .name("User")
                .email("user@ya.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Iron")
                .requester(user)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        item = Item.builder()
                .id(1L)
                .name("Iron")
                .description("Iron iron")
                .owner(user)
                .available(true)
                .request(itemRequest)
                .build();
    }

    @Test
    @SneakyThrows
    void itemFullDtoSerializationTest() {
        ItemDto itemDto = ItemMapper.toDto(item);
        JsonContent<ItemDto> itemDtoJsonContent = itemFullDtoJacksonTester.write(itemDto);

        assertThat(itemDtoJsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(Math.toIntExact(item.getId()));
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue("$.name")
                .isEqualTo(item.getName());
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(item.getDescription());
        assertThat(itemDtoJsonContent).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(item.isAvailable());
        assertThat(itemDtoJsonContent).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(Math.toIntExact(item.getRequest().getId()));

        assertThat(itemDtoJsonContent)
                .extractingJsonPathNumberValue("$.owner.id")
                .isEqualTo(Math.toIntExact(item.getOwner().getId()));
        assertThat(itemDtoJsonContent)
                .extractingJsonPathStringValue("$.owner.name").isEqualTo(item.getOwner().getName());
    }

    @Test
    @SneakyThrows
    void itemShortDtoSerializationTest() {
        ItemShortDto itemShortDto = ItemMapper.toShortDto(item);
        JsonContent<ItemShortDto> itemShortDtoJsonContent = itemShortDtoJacksonTester.write(itemShortDto);

        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(item.getId()));
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathStringValue("$.name").isEqualTo(item.getName());
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathStringValue("$.description").isEqualTo(item.getDescription());
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(item.isAvailable());
        assertThat(itemShortDtoJsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(Math.toIntExact(item.getRequest().getId()));
    }
}