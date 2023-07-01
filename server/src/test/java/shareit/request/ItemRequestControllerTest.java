package shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestServiceMock;

    private ItemRequestDto requestDto1;
    private ItemRequestDto requestDto2;
    private ItemRequestCreationDto requestCreationDto1;

    @BeforeEach
    void beforeEach() {
        requestDto1 = ItemRequestDto.builder()
                .id(1L)
                .requester(new UserShortDto(1L, "Requester1"))
                .description("Descr1")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        requestDto2 = ItemRequestDto.builder()
                .id(2L)
                .requester(new UserShortDto(1L, "Requester1"))
                .description("Descr2")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        requestCreationDto1 = new ItemRequestCreationDto(requestDto1.getDescription());
    }

    @SneakyThrows
    @Test
    void shouldReturnListOfRequestsWhenGettingByRequesterId() {
        when(itemRequestServiceMock.getByRequester(Mockito.anyLong())).thenReturn(List.of(requestDto1, requestDto2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$[0].created").value(requestDto1.getCreated().toString()))
                .andExpect(jsonPath("$[0].requester.id").value(requestDto1.getRequester().getId()))
                .andExpect(jsonPath("$[0].requester.name").value(requestDto1.getRequester().getName()))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].created").value(requestDto2.getCreated().toString()))
                .andExpect(jsonPath("$[1].requester.id").value(requestDto2.getRequester().getId()))
                .andExpect(jsonPath("$[1].requester.name").value(requestDto2.getRequester().getName()));
        verify(itemRequestServiceMock).getByRequester(1L);
    }

    @SneakyThrows
    @Test
    void shouldReturnListOfRequestsWhenGettingAllRequests() {
        when(itemRequestServiceMock.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(requestDto1, requestDto2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$[0].created").value(requestDto1.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[0].requester.id").value(requestDto1.getRequester().getId()))
                .andExpect(jsonPath("$[0].requester.name").value(requestDto1.getRequester().getName()))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].created").value(requestDto2.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$[1].requester.id").value(requestDto2.getRequester().getId()))
                .andExpect(jsonPath("$[1].requester.name").value(requestDto2.getRequester().getName()));
        verify(itemRequestServiceMock).getAll(1L, 1, 20);
    }

    @SneakyThrows
    @Test
    void shouldReturnRequestDtoWhenGettingById() {
        when(itemRequestServiceMock.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(requestDto1);

        mockMvc.perform(get("/requests/{requestId}", requestDto1.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto1.getId()))
                .andExpect(jsonPath("$.description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto1.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.requester.id").value(requestDto1.getRequester().getId()))
                .andExpect(jsonPath("$.requester.name").value(requestDto1.getRequester().getName()));
        verify(itemRequestServiceMock).getById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void shouldReturnRequestDtoWhenCreatingRequest() {
        when(itemRequestServiceMock.create(Mockito.anyLong(), Mockito.any(ItemRequestCreationDto.class)))
                .thenReturn(requestDto1);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestCreationDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto1.getId()))
                .andExpect(jsonPath("$.description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto1.getCreated()
                        .truncatedTo(ChronoUnit.SECONDS).toString()))
                .andExpect(jsonPath("$.requester.id").value(requestDto1.getRequester().getId()))
                .andExpect(jsonPath("$.requester.name").value(requestDto1.getRequester().getName()));
        verify(itemRequestServiceMock).create(1L, requestCreationDto1);
    }
}