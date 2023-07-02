package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private User dbUser;
    private Item dbItem;
    private ItemRequest dbItemRequest;
    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .name("User")
                .email("user@ya.ru")
                .build();
        dbUser = userRepository.save(user);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("Iron")
                .requester(user)
                .created(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .build();
        dbItemRequest = itemRequestRepository.save(itemRequest);

        Item item = Item.builder()
                .name("Iron")
                .description("Iron iron")
                .owner(dbUser)
                .available(true)
                .request(dbItemRequest)
                .build();
        dbItem = itemRepository.save(item);

        pageable = PageRequest.of(1 / 20, 20, Sort.by("id").descending());
    }

    @Test
    void shouldReturnListOfSingleItemWhenFindingByValidOwnerId() {
        assertEquals(List.of(dbItem), itemRepository.findAllByOwnerId(dbUser.getId(), pageable));
    }

    @Test
    void shouldReturnEmptyListWhenFindingByWrongOwnerId() {
        assertEquals(List.of(), itemRepository.findAllByOwnerId(999L, pageable));
    }

    @Test
    void shouldReturnListOfSingleItemWhenSearchingByText() {
        assertEquals(List.of(dbItem), itemRepository.search("iro", pageable));
    }

    @Test
    void shouldReturnEmptyListWhenSearchingNonExistentItem() {
        assertEquals(List.of(), itemRepository.search("jump", pageable));
    }

    @Test
    void shouldReturnListOfSingleItemWhenFindingByValidRequestId() {
        assertEquals(List.of(dbItem), itemRepository.findAllByRequestId(dbItemRequest.getId()));
    }

    @Test
    void shouldReturnListOfSingleItemWhenFindingByWrongRequestId() {
        assertEquals(List.of(), itemRepository.findAllByRequestId(999L));
    }

    @Test
    void shouldReturnListOfItemsWhenFindingByCorrectRequestIds() {
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("Iron")
                .requester(user)
                .created(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .build();
        ItemRequest dbItemRequest2 = itemRequestRepository.save(itemRequest2);

        Item item2 = Item.builder()
                .name("Iron")
                .description("Iron iron")
                .owner(dbUser)
                .available(true)
                .request(dbItemRequest2)
                .build();
        Item dbItem2 = itemRepository.save(item2);

        List<Long> ids = List.of(dbItemRequest.getId(), dbItemRequest2.getId());

        assertEquals(List.of(dbItem, dbItem2), itemRepository.findAllByRequestIdIn(ids));
    }

    @Test
    void shouldReturnEmptyWhenFindingByListOfFakeIdsOrEmpty() {
        List<Long> idsFake = List.of(999L, 1000L);
        List<Long> idsEmpty = List.of();

        assertEquals(List.of(), itemRepository.findAllByRequestIdIn(idsFake));
        assertEquals(List.of(), itemRepository.findAllByRequestIdIn(idsEmpty));
    }
}