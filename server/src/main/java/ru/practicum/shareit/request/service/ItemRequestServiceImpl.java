package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto create(long userId, ItemRequestCreationDto dto) {
        log.info("Received creation request with userId {} and description {}",
                userId, dto.getDescription());

        User user = userService.getUserById(userId);
        ItemRequest request = ItemRequestMapper.fromDto(dto, user);
        request = requestRepository.save(request);

        log.info("Request registered in DB with ID {}", request.getId());
        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getByRequester(long userId) {
        log.info("Received get-request with userId {}.", userId);

        User user = userService.getUserById(userId);
        List<ItemRequestDto> requestDtos = requestRepository
                .findAllByRequesterIdOrderByCreatedDesc(user.getId())
                .stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
        addDataToRequestDtos(requestDtos);

        log.info("Found {} requests.", requestDtos.size());
        return requestDtos;
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        log.info("Received get-request with userId = {}, from = {}, size = {}.",
                userId, from, size);

        User user = userService.getUserById(userId);
        Pageable pageable = getPage(from, size);
        List<ItemRequestDto> requestDtos = requestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(user.getId(), pageable)
                .stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
        addDataToRequestDtos(requestDtos);

        log.info("Found {} requests.", requestDtos.size());
        return requestDtos;
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        log.info("Received get-request with user ID {} and request ID {}", userId, requestId);

        User user = userService.getUserById(userId);
        ItemRequest request = getRequestById(requestId);

        ItemRequestDto dto = requestRepository.findById(request.getId())
                .map(ItemRequestMapper::toDto)
                .orElseThrow();

        dto.setItems(itemRepository.findAllByRequestId(dto.getId())
                .stream()
                .map(ItemMapper::toShortDto)
                .collect(Collectors.toList()));

        log.info("Found request with ID {} for user {}", dto.getId(), user.getId());
        return dto;
    }

    @Override
    public ItemRequest getRequestById(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with this ID not found."));
    }

    private void addDataToRequestDtos(List<ItemRequestDto> requestDtos) {
        List<Long> requestDtoIds = requestDtos.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestDtoIds);

        requestDtos.forEach(requestDto -> requestDto.setItems(
                items.stream()
                        .filter(item -> item.getRequest().getId() == requestDto.getId())
                        .map(ItemMapper::toShortDto)
                        .collect(Collectors.toList())));
    }

    private PageRequest getPage(int from, int size) {
        if (size <= 0 || from < 0) {
            throw new ValidationException("Size must not be less than 1, from must be above 0.");
        }
        return PageRequest.of(from / size, size, Sort.by("created").descending());
    }
}

