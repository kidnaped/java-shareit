import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.request.ItemRequestClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {
    private final RestTemplateBuilder builder = new RestTemplateBuilder();
    private final ItemRequestClient client = new ItemRequestClient("http://localhost:9090",builder);

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongSize() {
        assertThrows(IllegalArgumentException.class, () ->
                client.getAll(1L, 1, 0));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongFrom() {
        assertThrows(IllegalArgumentException.class, () ->
                client.getAll(1L, -1, 20));
    }

}