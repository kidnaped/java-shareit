package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemServiceMock;

    private ItemDto itemCreationDto1;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private CommentDto commentDto;
    private CommentDto commentCreationDto;

    @BeforeEach
    void beforeEach() {
        itemCreationDto1 = ItemDto.builder()
                .id(1L)
                .name("ItemCreation")
                .description("Item descr")
                .available(true)
                .build();
                new ItemDto();
        Item item1 = Item.builder()
                .id(1L)
                .name("OtherItem")
                .description("Other descr")
                .owner(new User(1L, "New User", "email.com"))
                .request(new ItemRequest(1L, "Request",
                        new User(2L, "Requester", "2email.com"),
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .build();

        UserShortDto userShortDto1 = new UserShortDto(1L, "");
        itemDto1 = ItemDto.builder().owner(userShortDto1).build();
        itemDto1 = ItemMapper.toDto(ItemMapper.fromDto(itemCreationDto1, item1));

        Item item2 = Item.builder()
                .id(2L)
                .name("OtherItem2")
                .description("Other descr2")
                .available(true)
                .owner(new User(1L, "New User2", "email2.com"))
                .request(new ItemRequest(2L, "Request2",
                        new User(2L, "Requester2", "222@email.com"),
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .build();

        UserShortDto userShortDto2 = new UserShortDto(1L, "");
        itemDto2 = ItemDto.builder().owner(userShortDto2).build();
        itemDto2.setOwner(userShortDto2);
        itemDto2 = ItemMapper.toDto(ItemMapper.fromDto(itemCreationDto1, item2));

        commentDto = CommentDto.builder()
                .id(1L)
                .authorName("Author Name")
                .text("Some text")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        commentCreationDto = CommentDto.builder().text(commentDto.getText()).build();
    }

    @Test
    @SneakyThrows
    void shouldReturnItemDtoWhenRegisterNewItem() {
        when(itemServiceMock.registerItem(1L, itemCreationDto1)).thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemCreationDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(itemDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.name").value(itemDto1.getOwner().getName()))
                .andExpect(jsonPath("$.requestId").value(itemDto1.getRequestId()));
        verify(itemServiceMock).registerItem(1L, itemCreationDto1);
    }

    @Test
    @SneakyThrows
    void shouldReturnItemDtoWhenGetByItemId() {
        when(itemServiceMock.getById(1L, 1L)).thenReturn(itemDto1);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(itemDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.name").value(itemDto1.getOwner().getName()))
                .andExpect(jsonPath("$.requestId").value(itemDto1.getRequestId()));
        verify(itemServiceMock).getById(1L, 1L);
    }

    @Test
    @SneakyThrows
    void shouldReturnUpdatedItemDtoWhenUpdatingExistingItem() {
        when(itemServiceMock.updateItem(1L, 1L, itemCreationDto1)).thenReturn(itemDto1);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemCreationDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(itemDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.name").value(itemDto1.getOwner().getName()))
                .andExpect(jsonPath("$.requestId").value(itemDto1.getRequestId()));
        verify(itemServiceMock).updateItem(1L, 1L, itemCreationDto1);
    }

    @Test
    @SneakyThrows
    void shouldReturnItemsListWhenGettingByUserId() {
        when(itemServiceMock.getUsersItems(1L, 1, 20))
                .thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$[0].owner.id").value(itemDto1.getOwner().getId()))
                .andExpect(jsonPath("$[0].owner.name").value(itemDto1.getOwner().getName()))
                .andExpect(jsonPath("$[0].requestId").value(itemDto1.getRequestId()))
                .andExpect(jsonPath("$[1].id").value(itemDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(itemDto2.getName()))
                .andExpect(jsonPath("$[1].description").value(itemDto2.getDescription()))
                .andExpect(jsonPath("$[1].available").value(itemDto2.getAvailable()))
                .andExpect(jsonPath("$[1].owner.id").value(itemDto2.getOwner().getId()))
                .andExpect(jsonPath("$[1].owner.name").value(itemDto2.getOwner().getName()))
                .andExpect(jsonPath("$[1].requestId").value(itemDto2.getRequestId()));
        verify(itemServiceMock).getUsersItems(1L, 1, 20);
    }

    @Test
    @SneakyThrows
    void shouldReturnItemWhenSearchingAvailableByText() {
        when(itemServiceMock.searchAvailableItems(Mockito.anyLong(),Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDto1));

        mockMvc.perform(get("/items/search", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "ItemCreation")
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto1.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$[0].owner.id").value(itemDto1.getOwner().getId()))
                .andExpect(jsonPath("$[0].owner.name").value(itemDto1.getOwner().getName()))
                .andExpect(jsonPath("$[0].requestId").value(itemDto1.getRequestId()));
        verify(itemServiceMock).searchAvailableItems(1L, "ItemCreation".toLowerCase(), 1, 20);
    }

    @Test
    @SneakyThrows
    void shouldReturnCommentWhenAddItToExistingItem() {
        when(itemServiceMock.addComment(1L, 1L, commentCreationDto)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentCreationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString()));
        verify(itemServiceMock).addComment(1L, 1L, commentCreationDto);
    }
}