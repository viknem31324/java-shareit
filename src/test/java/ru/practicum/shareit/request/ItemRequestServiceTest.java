package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.error.exception.ItemRequestValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestServiceIml;

    private LocalDateTime dateTime;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private Item item;
    private User user;

    @BeforeEach
    void beforeEach() {
        Clock clock = Clock.fixed(Instant.parse("2024-05-30T10:15:30.00Z"), ZoneId.of("UTC"));
        dateTime = LocalDateTime.now(clock);

        user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setCreated(dateTime);
        itemRequest.setRequestor(user);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setUser(user);
        item.setRequest(itemRequest);

        itemRequestDto = new ItemRequestDto(1L, "description", 1L,
                dateTime);

        itemRequestDtoResponse = new ItemRequestDtoResponse(1L, "description", dateTime, List.of(itemDto));
    }

    @Test
    public void createNewItemRequest_whenCreateNewItemRequest_shouldReturnItemRequestDto() {
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        when(userService.findById(anyLong()))
                .thenReturn(user);

        ItemRequestDto saveItemRequestDto = itemRequestServiceIml.createNewItemRequest(1L, itemRequestDto);

        assertItemRequest(itemRequest, saveItemRequestDto);
    }

    @Test
    public void createNewItemRequest_whenCreateNewItemRequestWitchIncorrectData_shouldThrowsException() {
        itemRequestDto = new ItemRequestDto(1L, null, 1L,
                dateTime);

        ItemRequestValidationException exception = assertThrows(ItemRequestValidationException.class, () ->
                itemRequestServiceIml.createNewItemRequest(1L, itemRequestDto));

        assertThat(exception.getMessage(), is("Некорректные данные!"));
    }

    @Test
    public void getItemRequestsWitchInfo_whenInvoked_shouldReturnCollectionItemRequestDtoResponse() {
        when(itemRepository.findItemsForItemRequestIds(any()))
                .thenReturn(List.of(List.of(item)));

        when(userService.findById(anyLong()))
                .thenReturn(user);

        when(itemRequestRepository.findAllItemRequestsByUserId(any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDtoResponse> itemRequestDtoResponses = itemRequestServiceIml.getItemRequestsWitchInfo(anyLong());
        assertThat(itemRequestDtoResponses.size(), is(1));
        assertThat(itemRequestDtoResponses.get(0), is(itemRequestDtoResponse));
    }

    @Test
    public void getAllItemRequestsWitchPagination_whenInvoked_shouldReturnCollectionItemRequestDtoResponse() {
        when(itemRepository.findItemsForItemRequestIds(any()))
                .thenReturn(List.of(List.of(item)));

        when(itemRequestRepository.getAllItemRequestsWitchPagination(any(), any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDtoResponse> itemRequestDtoResponses = itemRequestServiceIml
                .getAllItemRequestsWitchPagination(1L, 0, 10);

        assertThat(itemRequestDtoResponses.size(), is(1));
        assertThat(itemRequestDtoResponses.get(0), is(itemRequestDtoResponse));
    }

    @Test
    public void getInfoItemRequestById_whenFoundItemRequestDtoResponse_shouldReturnItemRequestDtoResponse() {
        when(userService.findById(anyLong()))
                .thenReturn(user);

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository.findItemsByRequestId(anyLong()))
                .thenReturn(List.of(item));

        ItemRequestDtoResponse savedItemRequestDtoResponse = itemRequestServiceIml
                .getInfoItemRequestById(1L, 1L);

        assertThat(savedItemRequestDtoResponse.getId(), is(1L));
        assertThat(savedItemRequestDtoResponse.getDescription(), is("description"));
        assertThat(savedItemRequestDtoResponse.getCreated(), is(dateTime));
        assertThat(savedItemRequestDtoResponse.getItems(), is(List.of(itemDto)));
    }

    @Test
    public void getInfoItemRequestById_whenNotFoundItemRequestDtoResponse_shouldThrowsException() {
        when(userService.findById(anyLong()))
                .thenReturn(user);

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class, () ->
                itemRequestServiceIml
                        .getInfoItemRequestById(1L, 1L));

        assertThat(exception.getMessage(), is("Запрос на вещь не найден!"));
    }

    private void assertItemRequest(ItemRequest itemRequest, ItemRequestDto itemRequestDto) {
        assertThat(itemRequest.getId(), is(itemRequestDto.getId()));
        assertThat(itemRequest.getCreated(), is(itemRequestDto.getCreated()));
        assertThat(itemRequest.getDescription(), is(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequestor().getId(), is(itemRequestDto.getRequestorId()));
    }
}
