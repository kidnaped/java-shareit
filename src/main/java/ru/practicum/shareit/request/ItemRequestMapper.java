package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(UserMapper.toShortDto(request.getRequester()))
                .created(request.getCreated().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    public static ItemRequest fromDto(ItemRequestCreationDto dto, User user) {
        ItemRequest request = new ItemRequest();
        request.setRequester(user);
        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return request;
    }
}
