package ru.practicum.shareit.error.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(final String message) {
        super(message);
    }
}
