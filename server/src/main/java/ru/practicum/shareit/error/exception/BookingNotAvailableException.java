package ru.practicum.shareit.error.exception;

public class BookingNotAvailableException extends RuntimeException {
    public BookingNotAvailableException(final String message) {
        super(message);
    }
}
