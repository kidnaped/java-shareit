package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRequestService itemRequestService;

    private UserDto userDto1;
    private UserDto userDto2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;
    ItemShortDto itemShortDto1;
    ItemShortDto itemShortDto2;
    ItemDto itemCreationDto1;

    @BeforeEach
    void beforeEach() {
        UserDto userDto1 = UserDto.builder().name("UserDto1").email("userDto1Email").build();
        UserDto userDto2 = UserDto.builder().name("UserDto2").email("userDto2Email").build();
        this.userDto1 = userService.createUser(userDto1);
        this.userDto2 = userService.createUser(userDto2);
        ItemRequestCreationDto itemRequestCreationDto1 = new ItemRequestCreationDto("Iron");
        ItemRequestCreationDto itemRequestCreationDto2 = new ItemRequestCreationDto("Copper");
        itemRequestDto1 = itemRequestService.create(this.userDto1.getId(), itemRequestCreationDto1);
        itemRequestDto2 = itemRequestService.create(this.userDto2.getId(), itemRequestCreationDto2);
        itemCreationDto1 = ItemDto.builder()
                .name("Iron")
                .description("Iron iron")
                .available(true)
                .requestId(itemRequestDto1.getId())
                .build();

        ItemDto itemCreationDto2 = ItemDto.builder()
                .name("Copper")
                .description("Copper copper")
                .available(true)
                .requestId(itemRequestDto2.getId())
                .build();

        ItemDto itemDto1 = itemService.registerItem(this.userDto1.getId(), itemCreationDto1);
        ItemDto itemDto2 = itemService.registerItem(this.userDto2.getId(), itemCreationDto2);
        itemShortDto1 = ItemMapper.toShortDto(itemService.getItemById(itemDto1.getId()));
        itemShortDto2 = ItemMapper.toShortDto(itemService.getItemById(itemDto2.getId()));

        itemRequestDto1.setItems(itemRequestService.getById(itemRequestDto1.getRequester().getId(),
                itemRequestDto1.getId()).getItems());
        itemRequestDto2.setItems(itemRequestService.getById(itemRequestDto2.getRequester().getId(),
                itemRequestDto2.getId()).getItems());
    }

    @Test
    void shouldReturnRequestDtoAfterCreatingAndGettingById() {
        ItemRequestCreationDto itemRequestCreationDto = new ItemRequestCreationDto("Smth");
        ItemRequestDto itemRequestDto = itemRequestService.create(userDto1.getId(), itemRequestCreationDto);

        itemRequestDto.setItems(itemRequestService.getById(itemRequestDto.getRequester().getId(),
                itemRequestDto.getId()).getItems());

        assertThat(itemRequestService.getById(userDto2.getId(), itemRequestDto.getId())).isEqualTo(itemRequestDto);
    }

    @Test
    void shouldReturnListOfRequestsWhenGettingByRequesterId() {
        List<ItemRequestDto> result1 = itemRequestService.getByRequester(userDto1.getId());
        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.get(0)).isEqualTo(itemRequestDto1);

        List<ItemRequestDto> result2 = itemRequestService.getByRequester(userDto2.getId());
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.get(0)).isEqualTo(itemRequestDto2);
    }

    @Test
    void shouldReturnListOfRequestsWhenGettingAllByNotRequester() {
        List<ItemRequestDto> result1 = itemRequestService.getAll(userDto2.getId(), 1, 20);
        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.get(0)).isEqualTo(itemRequestDto1);

        List<ItemRequestDto> result2 = itemRequestService.getAll(userDto1.getId(), 1, 20);
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.get(0)).isEqualTo(itemRequestDto2);
    }

    @Test
    void shouldReturnRequestDtoWhenGettingByRequestId() {
        assertThat(itemRequestService.getById(userDto2.getId(), itemRequestDto1.getId()))
                .isEqualTo(itemRequestDto1);

        assertThat(itemRequestService.getById(userDto1.getId(), itemRequestDto2.getId()))
                .isEqualTo(itemRequestDto2);
    }
}