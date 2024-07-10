package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private Booking booking;
    private Item item;
    LocalDateTime dateTime;

    @BeforeEach
    void beforeEach() {
        Clock clock = Clock.fixed(Instant.parse("2024-05-30T10:15:30.00Z"), ZoneId.of("UTC"));
        dateTime = LocalDateTime.now(clock);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setUser(new User());

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(dateTime.plusHours(1));
        booking.setEnd(dateTime.plusHours(3));
        booking.setItem(item);
        booking.setStatus(Constant.BookingStatus.APPROVED);
        booking.setBooker(new User());

        bookingDto = new BookingDto(1L, dateTime.plusHours(1), dateTime.plusHours(3));
    }

    @Test
    public void addNewBookingRequest_whenAddedNewBooking_shouldReturnBooking() throws Exception {
        when(bookingService.addNewBookingRequest(any(), anyLong()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header(Constant.HEADER, 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())));
    }

    @Test
    public void approvedBooking_whenApprovedBooking_shouldReturnApprovedBooking() throws Exception {
        when(bookingService.approvedBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Constant.HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Constant.BookingStatus.APPROVED.toString())));
    }

    @Test
    public void getBookingInformation_whenGetBookingInformation_shouldReturnBookingInformation() throws Exception {
        when(bookingService.getBookingInformation(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    public void getBookingListForCurrentUser_whenGetBookingListForCurrentUser_shouldReturnBookingListForCurrentUser()
            throws Exception {
        when(bookingService.getBookingListForCurrentUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .header(Constant.HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())));
    }

    @Test
    public void getBookingListForOwner_whenGetBookingListForOwner_shouldReturnBookingListForOwner() throws Exception {
        when(bookingService.getBookingListForOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header(Constant.HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())));
    }
}
