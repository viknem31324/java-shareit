package ru.practicum.shareit.exception;

public class ValidationBookingException extends RuntimeException {
    public ValidationBookingException(final String message) {
        super(message);
    }
}
