package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;
    private LocalDateTime dateTime;
    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
    private ItemRequestDtoResponse itemRequestDtoResponse;

    @BeforeEach
    void beforeEach() {
        Clock clock = Clock.fixed(Instant.parse("2024-05-30T10:15:30.00Z"), ZoneId.of("UTC"));
        dateTime = LocalDateTime.now(clock);

        itemRequestDto = new ItemRequestDto(1L, "description", 1L,
                dateTime);

        itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description", dateTime, List.of(itemDto));
    }

    @Test
    public void createNewItemRequest_whenCreated_shouldReturnStatusOkAndItemRequestDto() throws Exception {
        when(itemRequestService.createNewItemRequest(any(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(Constant.HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.created", is(dateTime.toString())));
    }

    @Test
    public void getItemRequestsWitchInfo_whenInvoked_shouldReturnStatusOkAndCollectionItemRequestDto() throws Exception {
        when(itemRequestService.getItemRequestsWitchInfo(any()))
                .thenReturn(List.of(itemRequestDtoResponse));

        mvc.perform(get("/requests")
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].created", is(dateTime.toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    public void getAllItemRequestsWitchPagination_whenInvoked_shouldReturnStatusOkAndCollectionItemRequestDtoResponse()
            throws Exception {
        when(itemRequestService.getAllItemRequestsWitchPagination(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoResponse));

        mvc.perform(get("/requests/all")
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].created", is(dateTime.toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    public void getInfoItemRequestById_whenInvoked_shouldReturnStatusOkAndItemRequestDtoResponse() throws Exception {
        when(itemRequestService.getInfoItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoResponse);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(dateTime.toString())))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }
}
