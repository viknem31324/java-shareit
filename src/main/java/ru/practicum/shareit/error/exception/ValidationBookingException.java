package ru.practicum.shareit.error.exception;

public class ValidationBookingException extends RuntimeException {
    public ValidationBookingException(final String message) {
        super(message);
    }
}
