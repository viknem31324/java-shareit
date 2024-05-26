package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.exception.*;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ UserNotFoundException.class, ItemNotFoundException.class, NotEnoughRightsException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final RuntimeException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler({ ValidationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(final RuntimeException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler({ UserAlreadyExistException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleAlreadyExistException(final RuntimeException e) {
        return new ExceptionResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleThrowable(final Throwable e) {
        return new ExceptionResponse("Произошла непредвиденная ошибка.");
    }
}
