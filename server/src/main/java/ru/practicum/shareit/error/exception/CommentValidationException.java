package ru.practicum.shareit.error.exception;

public class CommentValidationException extends RuntimeException {
    public CommentValidationException(final String message) {
        super(message);
    }
}
