package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    @Override
    public ItemDto registerItem(int userId, ItemDto itemDto) {
        User user = getUserOrThrow(userId);
        Item item = Mapper.fromDto(itemDto);
        item.setOwner(user);
        item = itemRepo.addItem(item);
        return Mapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto dto) {
        User user = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);

        if (!item.getOwner().equals(user)) {
            throw new ValidationException("User is not owns this item!");
        }

        Mapper.fromDto(dto, item);
        item = itemRepo.updateItem(item);
        return Mapper.toDto(item);
    }

    @Override
    public ItemDto getById(int userId, int itemId) {
        getUserOrThrow(userId);
        return Mapper.toDto(getItemOrThrow(itemId));
    }

    @Override
    public List<ItemDto> getUsersItems(int userId) {
        User user = getUserOrThrow(userId);
        return itemRepo.getAllByUser(user)
                .stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(int userId, String text) {
        getItemOrThrow(userId);
        List<Item> items = itemRepo.getByTextParams(text);
        items.removeIf(item -> !item.isAvailable());

        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(int userId) {
        User user = userRepo.getById(userId);
        if (user == null) {
            throw new NotFoundException("User with this ID is not found!");
        }
        return user;
    }

    private Item getItemOrThrow(int itemId) {
        Item item = itemRepo.getById(itemId);
        if (item == null) {
            throw new NotFoundException("Item with this ID is not found");
        }
        return item;
    }
}
