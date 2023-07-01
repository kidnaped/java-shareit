package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(value = USER_ID_HEADER) long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return service.registerItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = USER_ID_HEADER) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(value = USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        return service.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsForOwner(@RequestHeader(value = USER_ID_HEADER) long userId,
                                             @Min(0) @RequestParam(defaultValue = "0") int from,
                                             @Min(1) @RequestParam(defaultValue = "10") int size) {
        return service.getUsersItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForItems(@RequestHeader(value = USER_ID_HEADER) long userId,
                                        @RequestParam String text,
                                        @Min(0) @RequestParam(defaultValue = "0") int from,
                                        @Min(1) @RequestParam(defaultValue = "10") int size) {
        return service.searchAvailableItems(userId, text.toLowerCase(), from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = USER_ID_HEADER) long userId,
                                 @Valid @RequestBody CommentDto dto,
                                 @PathVariable Long itemId) {
        return service.addComment(userId, itemId, dto);
    }
}
