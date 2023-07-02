package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.Utils.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto registerRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                          @Valid @RequestBody ItemRequestCreationDto dto) {
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getUsersRequests(@RequestHeader (USER_ID_HEADER) long userId) {
        return requestService.getByRequester(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_ID_HEADER) long userId,
                                       @Min(0) @RequestParam(defaultValue = "0") int from,
                                       @Min(1) @RequestParam(defaultValue = "10") int size) {
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long requestId) {
        return requestService.getById(userId, requestId);
    }
}
