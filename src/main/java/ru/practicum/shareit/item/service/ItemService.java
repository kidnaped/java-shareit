package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto registerItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto dto);

    ItemDto getById(long userId, long itemId);

    Item getItemById(long itemId);

    List<ItemDto> getUsersItems(long userId);

    List<ItemDto> searchAvailableItems(long userId, String text);

    CommentDto addComment(long userId, long itemId, CommentDto dto);
}
