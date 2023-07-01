package shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User dbUser;
    private ItemRequest dbItemRequest;

    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        User user = User.builder()
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

        pageable = PageRequest.of(1 / 20, 20, Sort.by("id").descending());
    }

    @Test
    void shouldReturnRequestListWhenFindingAllByRequesterId() {
        assertEquals(List.of(dbItemRequest),
                itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(dbUser.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenFindingAllByRequesterIdWithWrongId() {
        assertEquals(List.of(), itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(3L));
    }

    @Test
    void shouldReturnEmptyListWhenRequesterFindingAllRequestsById() {
        assertEquals(List.of(),
                itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(dbUser.getId(), pageable));
    }

    @Test
    void shouldReturnRequestListWhenUserFindingAllRequests() {
        assertEquals(List.of(dbItemRequest),
                itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(9999L, pageable));
    }
}