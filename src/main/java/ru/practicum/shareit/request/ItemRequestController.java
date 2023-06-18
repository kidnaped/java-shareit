package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto registerRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemRequestCreationDto dto) {
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getUsersRequests(@RequestHeader ("X-Sharer-User-Id") long userId) {
        return requestService.getByRequester(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        return requestService.getById(userId, requestId);
    }
}
