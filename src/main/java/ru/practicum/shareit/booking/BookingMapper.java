package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BookingMapper {
    public BookingDtoResponse mapToBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(), booking.getBooker().getId());
    }
}
