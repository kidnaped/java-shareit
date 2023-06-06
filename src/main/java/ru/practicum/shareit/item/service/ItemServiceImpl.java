package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepo;
    private final BookingRepository bookingRepo;
    private final CommentRepository commentRepo;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto registerItem(long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);
        log.info("Item {} with ID {} created.", item.getName(), item.getId());
        return ItemMapper.toDto(itemRepo.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto dto) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);

        if (!isUserOwnerOfItem(user, item)) {
            throw new NotFoundException("Can't find relation between User and Item!");
        }

        ItemMapper.fromDto(dto, item);
        item = itemRepo.save(item);
        ItemDto resultDto = ItemMapper.toDto(item);

        log.info("Item {} with ID {} updated.", item.getName(), itemId);
        return resultDto;
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        ItemDto dto = makeDtoWithBookingsAndComments(user, item);
        log.info("Item with ID {} found.", itemId);
        return dto;
    }

    @Override
    public List<ItemDto> getUsersItems(long userId) {
        User user = userService.getUserById(userId);
        return itemRepo.getAllByOwnerOrderByIdAsc(user)
                .stream()
                .map(item -> makeDtoWithBookingsAndComments(user, item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(long userId, String text) {
        User user = userService.getUserById(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepo.search(text);
        items.removeIf(item -> !item.isAvailable());
        log.info("Found {} corresponding items.", items.size());
        return items.stream()
                .map(item -> makeDtoWithBookingsAndComments(user, item))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with this ID is not found"));
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto dto) {
        User author = userService.getUserById(userId);
        Item item = getItemById(itemId);
        Comment comment = new Comment();

        validateItemBookingByUser(author, item);
        comment.setAuthor(author);
        comment.setItem(item);
        comment = commentRepo.save(CommentMapper.fromDto(dto, comment));

        log.info("Comment {} added to item {}.", comment.getId(), item.getId());
        return CommentMapper.toDto(comment);
    }

    private ItemDto makeDtoWithBookingsAndComments(User user, Item item) {
        ItemDto dto = ItemMapper.toDto(item);

        if (isUserOwnerOfItem(user, item)) {
            Booking next = bookingRepo
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(), LocalDateTime.now(), Status.APPROVED).orElse(null);
            dto.setNextBooking(next != null ? BookingMapper.toShortDto(next) : null);

            Booking last = bookingRepo
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                            item.getId(), LocalDateTime.now(), Status.APPROVED).orElse(null);
            dto.setLastBooking(last != null ? BookingMapper.toShortDto(last) : null);
        }

        dto.setComments(CommentMapper.toDto(commentRepo.findAllByItemId(item.getId())));

        return dto;
    }

    private void validateItemBookingByUser(User user, Item item) {
        if (!bookingRepo.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now())) {
            throw new ValidationException("User never booked this item.");
        }
    }

    private boolean isUserOwnerOfItem(User user, Item item) {
        return item.getOwner().equals(user);
    }
}
