package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user1;
    private Item item1;
    private ItemRequest itemRequest1;
    private ItemDto itemDto1;
    private ItemDto itemCreationDto1;
    private Comment comment;
    private CommentDto commentCreationDto;
    private Pageable pageable;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder().id(1L).name("User1").email("user1@mail.com").build();
        User user2 = User.builder().id(2L).name("User2").email("user2@mail.com").build();
        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Iron")
                .requester(user2)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("Iron")
                .description("Iron iron")
                .owner(user1)
                .request(itemRequest1)
                .available(true)
                .build();

        itemDto1 = ItemMapper.toDto(item1);
        itemCreationDto1 = ItemDto.builder()
                .id(item1.getId())
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.isAvailable())
                .requestId(itemRequest1.getId())
                .comments(new ArrayList<>())
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Nice")
                .author(user1)
                .item(item1)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        commentCreationDto = CommentDto.builder().text(comment.getText()).build();
        pageable = PageRequest.of(1 / 20, 20, Sort.by("id").ascending());
        itemDto1.setComments(new ArrayList<>());
    }

    @Test
    void shouldReturnItemDtoWhenSearchingByText() {
        when(itemRepository.search(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(List.of(item1));

        assertThat(itemService.searchAvailableItems(1L,"iro", 1, 20)).asList().contains(itemDto1);
    }

    @Test
    void shouldReturnEmptyWhenSearchingByWrongText() {
        assertThat(itemService.searchAvailableItems(1L,"", 1, 20).size()).isZero();
        verify(itemRepository, times(0)).search("", pageable);
    }

    @Test
    void shouldThrowExceptionWhenSizeParameterIsZero() {
        assertThrows(IllegalArgumentException.class, () -> itemService.searchAvailableItems(1L,"iro", 0, 0));
    }

    @Test
    void shouldReturnListOf1ItemDtoWhenGettingUsersItems() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(List.of(item1));

        assertThat(itemService.getUsersItems(1L, 1, 20)).asList().contains(itemDto1);
    }

    @Test
    void shouldThrowExceptionWhenGettingUsersItemWithWrongUserId() {
        when(userService.getUserById(Mockito.anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.getUsersItems(9, 1, 20));
    }

    @Test
    void shouldReturnItemDtoWhenGettingById() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));
        itemDto1.setComments(new ArrayList<>());
        assertThat(itemService.getById(1L, 1L)).isEqualTo(itemDto1);
    }

    @Test
    void shouldReturnExceptionWhenGettingByIdWithWrongItemId() {
        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 999L));
    }

    @Test
    void shouldReturnItemDtoWhenRegisteringItem() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(itemRequestService.getRequestById(Mockito.anyLong())).thenReturn(itemRequest1);
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item1);

        assertThat(itemService.registerItem(1L, itemCreationDto1)).isEqualTo(itemDto1);
    }

    @Test
    void shouldProperlyUpdateItemAndReturnUpdatedItemDto() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1)).thenReturn(item1);

        assertThat(itemService.updateItem(1L, 1L, itemCreationDto1)).isEqualTo(itemDto1);
    }

    @Test
    void shouldReturnItemDtoWhenGettingItemById() {
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));

        assertThat(itemService.getItemById(item1.getId())).isEqualTo(item1);
    }

    @Test
    void shouldThrowExceptionWhenGettingItemByIdWithWrongId() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L));
    }

    @Test
    void shouldAddCommentsToItemAndReturnCommentDto() {
        when(userService.getUserById(Mockito.anyLong())).thenReturn(user1);
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(1L, 1L, commentCreationDto);

        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(commentDto.getCreated().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(comment.getCreated().truncatedTo(ChronoUnit.SECONDS));
    }
}