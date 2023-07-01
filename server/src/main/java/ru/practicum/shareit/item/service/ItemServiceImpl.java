package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Generated;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ItemRequestService requestService;

    @Override
    @Transactional
    public ItemDto registerItem(long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);

        if (itemDto.getRequestId() != null) {
            item.setRequest(requestService.getRequestById(itemDto.getRequestId()));
        }
        item = itemRepo.save(item);

        log.info("Item {} with ID {} created.", item.getName(), item.getId());
        return makeDtoWithAllData(user, item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto dto) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);

        if (!isUserOwnerOfItem(user, item)) {
            throw new NotFoundException("User is not the owner of the item.");
        }

        ItemMapper.fromDto(dto, item);
        item = itemRepo.save(item);
        ItemDto resultDto = makeDtoWithAllData(user, item);

        log.info("Item {} with ID {} updated.", item.getName(), itemId);
        return resultDto;
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        ItemDto dto = makeDtoWithAllData(user, item);
        log.info("Item with ID {} found.", itemId);
        return dto;
    }

    @Override
    public List<ItemDto> getUsersItems(long userId, int from, int size) {
        User user = userService.getUserById(userId);
        Pageable pageable = getPage(from, size);
        List<Booking> bookings = new ArrayList<>(bookingRepo.findAllByOwnerId(userId, pageable));
        List<Item> items = itemRepo.findAllByOwnerId(userId, pageable);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = new ArrayList<>(commentRepo.findAllByItemIdIn(itemIds));

        return items.stream()
                .map(item -> makeDtoWithAllData(user, item, bookings, comments))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(long userId, String text, int from, int size) {
        User user = userService.getUserById(userId);
        Pageable pageable = getPage(from, size);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepo.search(text, pageable).stream()
                .filter(Item::isAvailable)
                .collect(Collectors.toList());
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = new ArrayList<>(commentRepo.findAllByItemIdIn(itemIds));
        List<Booking> bookings = new ArrayList<>(bookingRepo.findAllByOwnerId(userId, pageable));

        log.info("Found {} corresponding items.", items.size());
        return items.stream()
                .map(item -> makeDtoWithAllData(user, item, bookings, comments))
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

    private ItemDto makeDtoWithAllData(User user, Item item) {
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

    @Generated
    private ItemDto makeDtoWithAllData(User user, Item item, List<Booking> bookings, List<Comment> comments) {
        ItemDto dto = ItemMapper.toDto(item);
        if (isUserOwnerOfItem(user, item)) {
            Booking next = null;
            Booking last = null;

            for (Booking booking : bookings) {
                if (booking.getItem().equals(item) && booking.getStatus().equals(Status.APPROVED)) {
                    if (booking.getStart().isAfter(LocalDateTime.now())) {
                        if (next == null || booking.getStart().isBefore(next.getStart())) {
                            next = booking;
                        }
                    } else if (last == null || booking.getEnd().isAfter(last.getEnd())) {
                        last = booking;
                    }
                }
            }

            dto.setNextBooking(next != null ? BookingMapper.toShortDto(next) : null);
            dto.setLastBooking(last != null ? BookingMapper.toShortDto(last) : null);
        }

        List<CommentDto> commentDtos = comments.stream()
                .filter(comment -> comment.getItem().equals(item))
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        dto.setComments(commentDtos);

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

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
