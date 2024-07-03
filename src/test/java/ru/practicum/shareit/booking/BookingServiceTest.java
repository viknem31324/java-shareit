package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemServiceImpl itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private LocalDateTime dateTime;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;
    private User user1;
    private User user2;
    private BookingDto bookingDto;
    private Booking booking;
    private Booking bookingApproved;

    @BeforeEach
    void beforeEach() {
        Clock clock = Clock.fixed(Instant.parse("2024-07-02T10:15:30.00Z"), ZoneId.of("UTC"));
        dateTime = LocalDateTime.now(clock);

        user1 = new User();
        user1.setId(1L);
        user1.setName("test");
        user1.setEmail("test@test.com");

        user2 = new User();
        user2.setId(5L);
        user2.setName("test");
        user2.setEmail("test@test.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setCreated(dateTime);

        item1 = new Item();
        item1.setId(1L);
        item1.setName("name");
        item1.setDescription("description");
        item1.setAvailable(true);
        item1.setUser(user2);
        item1.setRequest(itemRequest);

        item2 = new Item();
        item2.setId(1L);
        item2.setName("name");
        item2.setDescription("description");
        item2.setAvailable(false);
        item2.setUser(user2);
        item2.setRequest(itemRequest);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(3));
        booking.setItem(item1);
        booking.setStatus(Constant.BookingStatus.WAITING);
        booking.setBooker(user1);

        bookingApproved = new Booking();
        bookingApproved.setId(1L);
        bookingApproved.setStart(LocalDateTime.now().plusHours(1));
        bookingApproved.setEnd(LocalDateTime.now().plusHours(3));
        bookingApproved.setItem(item1);
        bookingApproved.setStatus(Constant.BookingStatus.APPROVED);
        bookingApproved.setBooker(new User());

        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
    }

    @Test
    public void addNewBookingRequest_whenCreatedNewBooking_shouldReturnBooking() {
        when(userService.findById(anyLong()))
                .thenReturn(user1);

        when(itemService.findById(anyLong()))
                .thenReturn(item1);

        when(bookingRepository.findFirstByItemIdCurrentBooking(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        Booking saveBooking = bookingService.addNewBookingRequest(bookingDto, 1L);

        assertBooking(booking, saveBooking);
    }

    @Test
    public void addNewBookingRequest_whenCreatedNewBookingWitchIncorrectData_shouldThrowsException() {
        bookingDto = new BookingDto(1L, null, dateTime.minusDays(3));

        ValidationItemException exception = assertThrows(ValidationItemException.class, () ->
                bookingService.addNewBookingRequest(bookingDto, 1L));

        assertThat(exception.getMessage(), is("Не корректные данные бронирования!"));
    }

    @Test
    public void addNewBookingRequest_whenCreatedNewBookingIfYouAreOwner_shouldThrowsException() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        when(itemService.findById(anyLong()))
                .thenReturn(item1);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () ->
                bookingService.addNewBookingRequest(bookingDto, 5L));

        assertThat(exception.getMessage(), is("Нельзя взять вещь в аренду у самого себя!"));
    }

    @Test
    public void addNewBookingRequest_whenCreatedNewBookingNonAvailable_shouldThrowsException() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        when(itemService.findById(anyLong()))
                .thenReturn(item2);

        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class, () ->
                bookingService.addNewBookingRequest(bookingDto, 1L));

        assertThat(exception.getMessage(), is("Вещь недоступна для аренды!"));
    }

    @Test
    public void approvedBooking_whenApprovedBooking_shouldReturnBookingWitchStatusApproved() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(itemService.findById(anyLong()))
                .thenReturn(item1);

        when(bookingRepository.save(any()))
                .thenReturn(bookingApproved);

        Booking approvedBooking = bookingService.approvedBooking(5L, 1L, true);

        assertThat(approvedBooking.getId(), is(booking.getId()));
        assertThat(approvedBooking.getStatus(), is(Constant.BookingStatus.APPROVED));
    }

    @Test
    public void approvedBooking_whenApprovedBookingIfNotOwnerItem_shouldThrowsException() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(itemService.findById(anyLong()))
                .thenReturn(item1);

        NotEnoughRightsException exception = assertThrows(NotEnoughRightsException.class, () ->
                bookingService.approvedBooking(1L, 1L, true));

        assertThat(exception.getMessage(), is("Пользователь не является владельцем вещи!"));
    }

    @Test
    public void approvedBooking_whenApprovedBookingIfStatusIsApproved_shouldThrowsException() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);
        booking.setStatus(Constant.BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        when(itemService.findById(anyLong()))
                .thenReturn(item1);

        BookingNotAvailableException exception = assertThrows(BookingNotAvailableException.class, () ->
                bookingService.approvedBooking(5L, 1L, true));

        assertThat(exception.getMessage(), is("Вещь уже взята в аренду!"));
    }

    @Test
    public void getBookingInformation_whenNotEnoughRights_shouldReturnBookingInformation() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking bookingInformation = bookingService.getBookingInformation(5L, 1L);

        assertBooking(booking, bookingInformation);
    }

    @Test
    public void getBookingInformation_whenGetBookingInformation__shouldThrowsException() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        NotEnoughRightsException exception = assertThrows(NotEnoughRightsException.class, () ->
                bookingService.getBookingInformation(7L, 1L));

        assertThat(exception.getMessage(), is("Недостаточно прав для просмотра!"));
    }

    @Test
    public void getBookingListForCurrentUser_whenInvokedWitchStateAll_shouldReturnBookingListForCurrentUser() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        when(bookingRepository.findAllBookingById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForCurrentUser(1l,
                Constant.BookingState.ALL.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForCurrentUser_whenInvokedWitchStateWaiting_shouldReturnListBookingWitchStatusWaiting() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.WAITING);

        when(bookingRepository.findAllBookingByIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForCurrentUser(1l,
                Constant.BookingState.WAITING.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForCurrentUser_whenInvokedWitchStateRejected_shouldReturnListBookingWitchStatusRejected() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.REJECTED);

        when(bookingRepository.findAllBookingByIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForCurrentUser(1l,
                Constant.BookingState.REJECTED.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForCurrentUser_whenInvokedWitchStatePast_shouldReturnListBookingWitchStatusPast() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.CANCELED);

        when(bookingRepository.findAllPastBookingById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForCurrentUser(1l,
                Constant.BookingState.PAST.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForCurrentUser_whenInvokedWitchStateFuture_shouldReturnListBookingWitchStatusFuture() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.WAITING);

        when(bookingRepository.findAllFutureBookingById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForCurrentUser(1l,
                Constant.BookingState.FUTURE.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForCurrentUser_whenInvokedWitchStateCurrent_shouldReturnListBookingWitchStatusCurrent() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.APPROVED);

        when(bookingRepository.findAllCurrentBookingById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForCurrentUser(1l,
                Constant.BookingState.CURRENT.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForOwner_whenInvokedWitchStateAll_shouldReturnBookingListForCurrentUser() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        when(bookingRepository.findAllBookingOwnerById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForOwner(1l,
                Constant.BookingState.ALL.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForOwner_whenInvokedWitchStateWaiting_shouldReturnListBookingWitchStatusWaiting() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.WAITING);

        when(bookingRepository.findAllBookingOwnerByIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForOwner(1l,
                Constant.BookingState.WAITING.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForOwner_whenInvokedWitchStateRejected_shouldReturnListBookingWitchStatusRejected() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.REJECTED);

        when(bookingRepository.findAllBookingOwnerByIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForOwner(1l,
                Constant.BookingState.REJECTED.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForOwner_whenInvokedWitchStatePast_shouldReturnListBookingWitchStatusPast() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.CANCELED);

        when(bookingRepository.findAllPastOwnerBookingById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForOwner(1l,
                Constant.BookingState.PAST.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForOwner_whenInvokedWitchStateFuture_shouldReturnListBookingWitchStatusFuture() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.WAITING);

        when(bookingRepository.findAllFutureOwnerBookingById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForOwner(1l,
                Constant.BookingState.FUTURE.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    @Test
    public void getBookingListForOwner_whenInvokedWitchStateCurrent_shouldReturnListBookingWitchStatusCurrent() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        booking.setStatus(Constant.BookingStatus.APPROVED);

        when(bookingRepository.findAllCurrentOwnerBookingById(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<Booking> bookings = bookingService.getBookingListForOwner(1l,
                Constant.BookingState.CURRENT.toString(), 0, 10);

        assertBooking(booking, bookings.get(0));
    }

    private void assertBooking(Booking booking, Booking bookingFind) {
        assertThat(booking.getId(), is(bookingFind.getId()));
        assertThat(booking.getStart(), is(bookingFind.getStart()));
        assertThat(booking.getEnd(), is(bookingFind.getEnd()));
        assertThat(booking.getStatus(), is(bookingFind.getStatus()));
        assertThat(booking.getBooker(), is(bookingFind.getBooker()));
        assertThat(booking.getItem(), is(bookingFind.getItem()));
    }
}
