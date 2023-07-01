import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {
    private final RestTemplateBuilder builder = new RestTemplateBuilder();
    private final BookingClient client = new BookingClient("http://localhost:9090", builder);

    @Test
    void shouldThrowValidationExceptionWhenWrongEndTimeBeforeStart() {
        BookingInputDto dto = BookingInputDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().minusHours(1))
                .build();
        assertThrows(ValidationException.class, () -> client.create(1L, dto));
    }

    @Test
    void shouldThrowValidationExceptionWhenStartEqualsEnd() {
        BookingInputDto dto = BookingInputDto.builder()
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
        assertThrows(ValidationException.class, () -> client.create(1L, dto));
    }

    @Test
    void shouldThrowValidationExceptionWhenPassingWrongState() {
        assertThrows(ValidationException.class, () ->
                client.getByBookerId(1L, "CHIHUAHUA", 1, 20));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongFromAndSize() {
        assertThrows(IllegalArgumentException.class, () ->
                client.getByBookerId(1L, "ALL", 0, 0));
    }

    @Test
    void shouldThrowValidationExceptionWhenPassingWrongStateAndOwnerId() {
        assertThrows(ValidationException.class, () ->
                client.getByOwnerId(1L, "CHICHICHI", 1, 20));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPassingWrongFromOrSizeAndOwnerId() {
        assertThrows(IllegalArgumentException.class, () ->
                client.getByOwnerId(1L, "ALL", 0, 0));
    }
}