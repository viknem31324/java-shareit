package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDtoResponse mapToBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(booking.getId(), booking.getBooker().getId());
    }
}
