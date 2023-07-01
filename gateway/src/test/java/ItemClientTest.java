import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.item.ItemClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {
    private final RestTemplateBuilder builder = new RestTemplateBuilder();
    private final ItemClient client = new ItemClient("http://localhost:9090", builder);

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongSize() {
        assertThrows(IllegalArgumentException.class, () ->
                client.getByUserId(1L, 1, 0));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongFrom() {
        assertThrows(IllegalArgumentException.class, () ->
                client.getByUserId(1L, -1, 20));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongSizeWhileSearching() {
        assertThrows(IllegalArgumentException.class, () ->
                client.search(1L, "ALL", 1, 0));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongFromWhileSearching() {
        assertThrows(IllegalArgumentException.class, () ->
                client.search(1L, "ALL", -1, 20));
    }
}