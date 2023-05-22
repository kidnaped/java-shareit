package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto registerItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, int itemId, ItemDto dto);

    ItemDto getById(int userId, int itemId);

    List<ItemDto> getUsersItems(int userId);

    List<ItemDto> searchAvailableItems(int userId, String text);
}
