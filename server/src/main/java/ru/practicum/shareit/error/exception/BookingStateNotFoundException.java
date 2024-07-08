package ru.practicum.shareit.error.exception;

public class BookingStateNotFoundException extends RuntimeException {
    public BookingStateNotFoundException(final String message) {
        super(message);
    }
}
