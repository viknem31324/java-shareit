package ru.practicum.shareit.booking.repository;

import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.helpers.Constant.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomBookingRepository {
    List<Booking> findFirstByItemIdsNextBooking(@Param("itemIds") List<Long> itemIds,
                                                @Param("status") BookingStatus status,
                                                LocalDateTime time);

    List<Booking> findFirstByItemIdsLastBooking(@Param("itemIds") List<Long> itemIds,
                                                @Param("status") BookingStatus status,
                                                LocalDateTime time);
}
