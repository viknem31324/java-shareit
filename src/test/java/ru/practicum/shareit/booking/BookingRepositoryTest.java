package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void beforeEach() {
        User user1 = new User();
        user1.setEmail("test1@test.com");
        user1.setName("test1");

        Item item1 = new Item();
        item1.setName("test1");
        item1.setDescription("description");
        item1.setUser(user1);
        item1.setAvailable(true);

        Booking booking1 = new Booking();
        booking1.setBooker(user1);
        booking1.setItem(item1);
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(LocalDateTime.now().minusHours(12));
        booking1.setStatus(Constant.BookingStatus.APPROVED);

        User user2 = new User();
        user2.setEmail("test2@test.com");
        user2.setName("test2");

        Item item2 = new Item();
        item2.setName("test2");
        item2.setDescription("descriptionTest");
        item2.setUser(user2);
        item2.setAvailable(true);

        Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(2));
        booking2.setStatus(Constant.BookingStatus.APPROVED);

        User user3 = new User();
        user3.setEmail("test3@test.com");
        user3.setName("test3");

        Item item3 = new Item();
        item3.setName("test3");
        item3.setDescription("descriptionTest");
        item3.setUser(user3);
        item3.setAvailable(true);

        Booking booking3 = new Booking();
        booking3.setBooker(user3);
        booking3.setItem(item3);
        booking3.setStart(LocalDateTime.now().minusHours(1));
        booking3.setEnd(LocalDateTime.now().plusHours(3));
        booking3.setStatus(Constant.BookingStatus.APPROVED);

        entityManager.persist(user1);
        entityManager.persist(item1);
        entityManager.persist(booking1);

        entityManager.persist(user2);
        entityManager.persist(item2);
        entityManager.persist(booking2);

        entityManager.persist(user3);
        entityManager.persist(item3);
        entityManager.persist(booking3);
    }

    @Test
    public void findFirstByItemIdLastBookingTest() {
        List<Booking> result = bookingRepository.findFirstByItemIdLastBooking(1L,
                Constant.BookingStatus.APPROVED, LocalDateTime.now(), PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(1L));
    }

    @Test
    public void findFirstByItemIdNextBookingTest() {
        List<Booking> result = bookingRepository.findFirstByItemIdNextBooking(2L,
                Constant.BookingStatus.APPROVED, LocalDateTime.now(), PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(2L));
    }

    @Test
    public void findAllBookingByIdTest() {
        List<Booking> result = bookingRepository.findAllBookingById(1L, PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(1L));
    }

    @Test
    public void findAllBookingByIdAndStatusTest() {
        List<Booking> result = bookingRepository.findAllBookingByIdAndStatus(2L,
                Constant.BookingStatus.APPROVED, PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(2L));
    }

    @Test
    public void findAllPastBookingByIdTest() {
        List<Booking> result = bookingRepository.findAllPastBookingById(1L, PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(1L));
    }

    @Test
    public void findAllFutureBookingByIdTest() {
        List<Booking> result = bookingRepository.findAllFutureBookingById(2L, PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(2L));
    }

    @Test
    public void findAllCurrentBookingByIdTest() {
        List<Booking> result = bookingRepository.findAllCurrentBookingById(3L, PAGEABLE);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(3L));
    }
}
