package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    void beforeEach() {
        UserDto userCreationDto1 = UserDto.builder().name("UserCreation").email("email").build();
        UserDto userCreationDto2 = UserDto.builder().name("UserCreation2").email("email2").build();
        userDto1 = userService.createUser(userCreationDto1);
        userDto2 = userService.createUser(userCreationDto2);

        ItemDto itemCreationDto1 = ItemDto.builder()
                .name("ItemCreation")
                .description("Descr")
                .available(true)
                .build();
        ItemDto itemCreationDto2 = ItemDto.builder()
                .name("2ItemTion2")
                .description("2DeVscr2")
                .available(true)
                .build();

        itemDto1 = itemService.registerItem(userDto1.getId(), itemCreationDto1);
        itemDto2 = itemService.registerItem(userDto2.getId(), itemCreationDto2);
        itemDto1.setComments(new ArrayList<>());
        itemDto2.setComments(new ArrayList<>());
    }

    @Test
    void shouldReturnItemDtoWhenCreatingItemAndFindingById() {
        ItemDto itemCreationDto = ItemDto.builder()
                .name("ItemCreation")
                .description("Descr")
                .available(true)
                .build();
        ItemDto itemDto = itemService.registerItem(userDto1.getId(), itemCreationDto);

        assertThat(itemService.getById(userDto1.getId(), itemDto.getId())).isEqualTo(itemDto);
    }

    @Test
    void shouldReturnListOfDtosWhenSearchingByText() {
        List<ItemDto> result1 = itemService.searchAvailableItems(userDto1.getId(),"Item", 1, 20);
        List<ItemDto> result2 = itemService.searchAvailableItems(userDto1.getId(), "Vscr", 1, 20);
        List<ItemDto> result3 = itemService.searchAvailableItems(userDto1.getId(),"Descr", 1, 20);
        List<ItemDto> result4 = itemService.searchAvailableItems(userDto1.getId(),"Nice", 1, 20);

        assertThat(result1).asList().containsExactly(itemDto1, itemDto2);
        assertThat(result2).asList().containsExactly(itemDto2);
        assertThat(result3).asList().containsExactly(itemDto1);
        assertThat(result4).isEmpty();
    }

    @Test
    void shouldReturnItemDtoListWhenGettingByUserId() {
        assertThat(itemService.getUsersItems(userDto1.getId(), 0, 20).get(0)).isEqualTo(itemDto1);
        assertThat(itemService.getUsersItems(userDto2.getId(), 0, 20).get(0)).isEqualTo(itemDto2);
    }

    @Test
    void shouldReturnItemWhenGettingById() {
        assertThat(itemService.getById(userDto1.getId(), itemDto1.getId())).isEqualTo(itemDto1);
        assertThat(itemService.getById(userDto2.getId(), itemDto2.getId())).isEqualTo(itemDto2);
    }
}
