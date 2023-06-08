package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester())
                .description(request.getDescription())
                .build();
    }

    public static ItemRequest fromDto(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .requester(requestDto.getRequester())
                .description(requestDto.getDescription())
                .build();
    }
}
