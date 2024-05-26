package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Booking {
    Long id;
    LocalDate start;
    LocalDate end;
    Item item;
    User booker;
    BookingStatus status;
}
