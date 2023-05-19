package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return service.registerItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                              @PathVariable int itemId,
                              @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                               @PathVariable int itemId) {
        return service.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsForOwner(@RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return service.getUsersItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(@RequestHeader(value = "X-Sharer-User-Id") int userId,
                                        @RequestParam String text) {
        return service.searchAvailableItems(userId, text.toLowerCase());
    }
}
