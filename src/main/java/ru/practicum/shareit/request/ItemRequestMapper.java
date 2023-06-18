package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(UserMapper.toShortDto(request.getRequester()))
                .creationDate(request.getCreationDate())
                .build();
    }

    public static ItemRequest fromDto(ItemRequestCreationDto dto, User user) {
        ItemRequest request = new ItemRequest();
        request.setRequester(user);
        request.setDescription(dto.getDescription());
        request.setCreationDate(LocalDateTime.now());
        return request;
    }
}
