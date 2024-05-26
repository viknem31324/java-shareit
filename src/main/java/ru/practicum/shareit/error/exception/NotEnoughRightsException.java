package ru.practicum.shareit.error.exception;

public class NotEnoughRightsException extends RuntimeException {
    public NotEnoughRightsException(final String message) {
        super(message);
    }
}
