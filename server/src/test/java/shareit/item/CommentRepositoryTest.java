package shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private Item dbItem;
    private Comment dbComment;
    private User dbUser;

    @BeforeEach
    public void beforeEach() {
        User user = User.builder()
                .name("User")
                .email("user@ya.ru")
                .build();
        dbUser = userRepository.save(user);

        Item item = Item.builder()
                .name("Iron")
                .description("Iron iron")
                .owner(dbUser)
                .available(true)
                .build();
        dbItem = itemRepository.save(item);

        Comment comment = Comment.builder()
                .text("Nice!")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .item(dbItem)
                .author(dbUser)
                .build();
                new Comment();
        dbComment = commentRepository.save(comment);
    }

    @Test
    void shouldReturnListOfSingleCommentWhenFindingByValidItemId() {
        assertEquals(List.of(dbComment), commentRepository.findAllByItemId(dbItem.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenFindingByWrongItemId() {
        assertEquals(List.of(), commentRepository.findAllByItemId(999L));
    }

    @Test
    void shouldReturnListOfCommentsWhenFindingByCorrectItemIds() {
        Item item2 = Item.builder()
                .name("Iron")
                .description("Iron iron")
                .owner(dbUser)
                .available(true)
                .build();
        Item dbItem2 = itemRepository.save(item2);

        Comment comment2 = Comment.builder()
                .text("2Nice!")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .item(dbItem)
                .author(dbUser)
                .build();
        new Comment();
        Comment dbComment2 = commentRepository.save(comment2);

        List<Long> ids = List.of(dbItem.getId(), dbItem2.getId());

        assertEquals(List.of(dbComment, dbComment2), commentRepository.findAllByItemIdIn(ids));
    }

    @Test
    void shouldReturnEmptyWhenFindingByListOfFakeIdsOrEmpty() {
        List<Long> idsFake = List.of(999L, 1000L);
        List<Long> idsEmpty = List.of();

        assertEquals(List.of(), commentRepository.findAllByItemIdIn(idsFake));
        assertEquals(List.of(), commentRepository.findAllByItemIdIn(idsEmpty));
    }
}