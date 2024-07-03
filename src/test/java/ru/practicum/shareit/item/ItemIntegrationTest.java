package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class ItemIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;

    @BeforeEach
    void beforeEach() {
        userDto1 = new UserDto(1L, "test1@test.com", "name1");
        userDto2 = new UserDto(2L, "test2@test.com", "name2");
        itemDto1 = new ItemDto(1L, "Дрель", "Классная дрель", true,
                null);
        bookingDto1 = new BookingDto(1L, LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        bookingDto2 = new BookingDto(1L, LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(11));
    }

    @Test
    public void findAllItemsForUser_whenInvoked_shouldReturnAllItemsWitchLastAndNextBooking()
            throws InterruptedException {
        userService.createUser(userDto1);
        userService.createUser(userDto2);

        itemService.addItem(itemDto1, 1L);

        bookingService.addNewBookingRequest(bookingDto1, 2L);
        bookingService.addNewBookingRequest(bookingDto2, 2L);

        bookingService.approvedBooking(1L, 1L, true);
        bookingService.approvedBooking(1L, 2L, true);

        Thread.sleep(5000);

        List<ItemDtoBooking> items = itemService.findAllItemsForUser(1L, 0, 10);

        assertThat(items.get(0).getId(), is(1L));
        assertThat(items.get(0).getName(), is(itemDto1.getName()));
        assertThat(items.get(0).getDescription(), is(itemDto1.getDescription()));
        assertThat(items.get(0).getAvailable(), is(true));
        assertThat(items.get(0).getLastBooking().getId(), is(1L));
        assertThat(items.get(0).getNextBooking().getId(), is(2L));
    }
}
