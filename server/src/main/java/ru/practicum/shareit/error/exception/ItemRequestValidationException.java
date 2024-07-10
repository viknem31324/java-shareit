package ru.practicum.shareit.error.exception;

public class ItemRequestValidationException extends RuntimeException {
    public ItemRequestValidationException(final String message) {
        super(message);
    }
}
