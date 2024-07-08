package ru.practicum.shareit.error.exception;

public class ValidationUserException extends RuntimeException {
    public ValidationUserException(final String message) {
        super(message);
    }
}
