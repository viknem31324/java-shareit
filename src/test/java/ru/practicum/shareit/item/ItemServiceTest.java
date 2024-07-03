package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDtoResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.BookingRepositoryImpl;
import ru.practicum.shareit.error.exception.NotEnoughRightsException;
import ru.practicum.shareit.error.exception.ValidationItemException;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingRepositoryImpl bookingRepositoryImpl;
    @Mock
    private CommentRepository commentRepository;
    private Item item;
    private ItemDto itemDtoCreated = new ItemDto(1L, "TestName", "TestDescription",
            true, null);
    private User user1;
    private User user2;
    private Booking booking1;
    private Booking booking2;
    private Comment comment;
    private CommentDto commentDto;
    private LocalDateTime currentTime;
    private BookingDtoResponse bookingDtoResponse1;
    private BookingDtoResponse bookingDtoResponse2;

    @BeforeEach
    void beforeEach() {
        currentTime = LocalDateTime.now();

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("test@test.com");
        user1.setName("Name");

        user2 = new User();
        user2.setId(99L);
        user2.setEmail("test99@test.com");
        user2.setName("Name99");

        item = new Item();
        item.setId(1L);
        item.setName("TestName");
        item.setDescription("TestDescription");
        item.setAvailable(true);
        item.setUser(user1);

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item);
        booking1.setBooker(user1);
        booking1.setStatus(Constant.BookingStatus.APPROVED);
        booking1.setStart(currentTime.minusDays(1));
        booking1.setEnd(currentTime.minusHours(12));

        bookingDtoResponse1 = new BookingDtoResponse(booking1.getId(), booking1.getBooker().getId());

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setItem(item);
        booking2.setBooker(user1);
        booking2.setStatus(Constant.BookingStatus.WAITING);
        booking2.setStart(currentTime.plusHours(5));
        booking2.setEnd(currentTime.plusDays(12));

        bookingDtoResponse2 = new BookingDtoResponse(booking2.getId(), booking2.getBooker().getId());

        comment = new Comment();
        comment.setId(1L);
        comment.setCreated(currentTime);
        comment.setItem(item);
        comment.setAuthor(user1);
        comment.setText("text comment");

        commentDto = new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(),
                comment.getCreated());
    }

    @Test
    public void addItem_whenCreatedItem_shouldReturnedItemDto() {
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto newItemDto = itemService.addItem(itemDtoCreated, 1L);

        assertItem(item, newItemDto);
    }

    @Test
    public void addItem_whenCreatedItemWitchIncorrectData_shouldThrowsException() {
        itemDtoCreated = new ItemDto(1L, null, null,
                null, null);

        ValidationItemException exception = assertThrows(ValidationItemException.class, () ->
                itemService.addItem(itemDtoCreated, 1L));

        assertThat(exception.getMessage(), is("Некорректные данные вещи!"));
    }

    @Test
    public void updateItem_whenUpdatedItem_shouldReturnUpdatedItem() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userService.findById(1L))
                .thenReturn(user1);

        ItemDto updateItemDto = new ItemDto(1L, "UpdateTestName", "TestDescription",
                true, null);

        Item updateItem = new Item();
        updateItem.setId(updateItemDto.getId());
        updateItem.setName(updateItemDto.getName());
        updateItem.setDescription(updateItemDto.getDescription());
        updateItem.setAvailable(updateItemDto.getAvailable());
        updateItem.setUser(user1);

        when(itemRepository.save(updateItem))
                .thenReturn(updateItem);

        ItemDto itemDto = itemService.updateItem(updateItemDto, 1L, 1L);

        assertEquals(updateItemDto.getName(), itemDto.getName());
        assertEquals(updateItemDto.getDescription(), itemDto.getDescription());
    }

    @Test
    public void updateItem_whenUpdatedItemIfUserNotOwner_shouldThrowsException() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userService.findById(99L))
                .thenReturn(user2);
        ItemDto updateItemDto = new ItemDto(1L, "UpdateTestName", "TestDescription",
                true, null);

        NotEnoughRightsException exception = assertThrows(NotEnoughRightsException.class, () ->
                itemService.updateItem(updateItemDto, 1L, 99L));

        assertThat(exception.getMessage(), is("Пользователь не является владельцем вещи!"));
    }

    @Test
    public void findItemDtoById_whenFoundItem_shouldReturnItemDto() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdNextBooking(anyLong(),
                any(), any(), any()))
                .thenReturn(List.of(booking2));
        when(bookingRepository.findFirstByItemIdLastBooking(anyLong(), any(),
                any(), any()))
                .thenReturn(List.of(booking1));
        when(commentRepository.findAllByItemId(1L))
                .thenReturn(List.of(comment));

        ItemDtoBooking itemDtoBooking = itemService.findItemDtoById(1L, 1L);

        assertThat(item.getId(), is(itemDtoBooking.getId()));
        assertThat(item.getName(), is(itemDtoBooking.getName()));
        assertThat(item.getDescription(), is(itemDtoBooking.getDescription()));
        assertThat(item.getAvailable(), is(itemDtoBooking.getAvailable()));
        assertThat(bookingDtoResponse1, is(itemDtoBooking.getLastBooking()));
        assertThat(bookingDtoResponse2, is(itemDtoBooking.getNextBooking()));
        assertThat(commentDto, is(itemDtoBooking.getComments().get(0)));
    }

    @Test
    public void findAllItemsForUser_whenFoundItems_shouldReturnCollectionItemDtoBooking() {
        when(itemRepository.findAllByUserId(anyLong(), any()))
                .thenReturn(List.of(item));

        when(commentRepository.findCommentsForItems(any()))
                .thenReturn(Map.of(1L, List.of(comment)));

        when(bookingRepositoryImpl.findFirstByItemIdsLastBooking(any(), any(), any()))
                .thenReturn(List.of(booking1));

        when(bookingRepositoryImpl.findFirstByItemIdsNextBooking(any(), any(), any()))
                .thenReturn(List.of(booking2));

        List<ItemDtoBooking> itemDtoBookings = itemService.findAllItemsForUser(1L, 0, 10);

        assertThat(item.getId(), is(itemDtoBookings.get(0).getId()));
        assertThat(item.getName(), is(itemDtoBookings.get(0).getName()));
        assertThat(item.getDescription(), is(itemDtoBookings.get(0).getDescription()));
        assertThat(item.getAvailable(), is(itemDtoBookings.get(0).getAvailable()));
        assertThat(bookingDtoResponse1, is(itemDtoBookings.get(0).getLastBooking()));
        assertThat(bookingDtoResponse2, is(itemDtoBookings.get(0).getNextBooking()));
        assertThat(commentDto, is(itemDtoBookings.get(0).getComments().get(0)));
    }

    @Test
    public void searchItems_whenFoundItems_shouldReturnCollectionItemDto() {
        when(itemRepository.findAllByContainsText(any(), any()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos = itemService.searchItems("текст", 0, 10);

        assertThat(item.getId(), is(itemDtos.get(0).getId()));
        assertThat(item.getName(), is(itemDtos.get(0).getName()));
        assertThat(item.getDescription(), is(itemDtos.get(0).getDescription()));
        assertThat(item.getAvailable(), is(itemDtos.get(0).getAvailable()));
    }

    private void assertItem(Item item, ItemDto itemDto) {
        assertThat(item.getId(), is(itemDto.getId()));
        assertThat(item.getName(), is(itemDto.getName()));
        assertThat(item.getDescription(), is(itemDto.getDescription()));
        assertThat(item.getAvailable(), is(itemDto.getAvailable()));
    }
}
