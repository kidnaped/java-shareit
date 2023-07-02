package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user1;
    private ItemRequest itemRequest1;
    private ItemRequestCreationDto requestCreationDto1;
    private ItemRequestDto itemRequestDto1;
    private Item item1;
    Pageable pageable;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder().name("User").email("user@ya.ru").build();
        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Iron")
                .requester(user1)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("Iron")
                .description("Iron iron")
                .owner(user1)
                .available(true)
                .request(itemRequest1)
                .build();
        requestCreationDto1 = new ItemRequestCreationDto(itemRequest1.getDescription());
        itemRequestDto1 = ItemRequestMapper.toDto(itemRequest1);
        itemRequestDto1.setItems(List.of(ItemMapper.toShortDto(item1)));
        pageable = PageRequest.of(1 / 20, 20, Sort.by("created").descending());
    }

    @Test
    void shouldReturnRequestDtoWhenGettingByRequester() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(requestRepository
                .findAllByRequesterIdOrderByCreatedDesc(Mockito.anyLong())).thenReturn(List.of(itemRequest1));
        when(itemRepository.findAllByRequestIdIn(Mockito.anyList())).thenReturn(List.of(item1));

        assertThat(requestService.getByRequester(user1.getId())).asList().contains(itemRequestDto1);
    }

    @Test
    void shouldReturnListOfRequestsWhenGettingAllByRequesterId() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(requestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(itemRequest1));
        when(itemRepository.findAllByRequestIdIn(Mockito.anyList())).thenReturn(List.of(item1));

        assertThat(requestService.getAll(user1.getId(), 1, 20)).asList().contains(itemRequestDto1);
    }

    @Test
    void shouldReturnRequestDtoAfterCreating() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(requestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest1);
        itemRequestDto1.setItems(null);

        assertThat(requestService.create(user1.getId(), requestCreationDto1)).isEqualTo(itemRequestDto1);
    }

    @Test
    void shouldReturnRequestDtoWhenGettingByRequestId() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(List.of(item1));

        assertThat(requestService.getById(user1.getId(), itemRequest1.getId())).isEqualTo(itemRequestDto1);
    }

    @Test
    void shouldReturnRequestWhenGettingByRequestId() {
        when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest1));

        assertThat(requestService.getRequestById(itemRequest1.getId())).isEqualTo(itemRequest1);
    }
}