package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;

import java.util.List;

public interface BookingService {
    Booking addNewBookingRequest(BookingDto bookingDto, long bookerId);

    Booking approvedBooking(long ownerId, long bookingId, boolean approved);

    Booking getBookingInformation(long userId, long bookingId);

    List<Booking> getBookingListForCurrentUser(long userId, String state, int from, int size);

    List<Booking> getBookingListForOwner(long userId, String state, int from, int size);
}
