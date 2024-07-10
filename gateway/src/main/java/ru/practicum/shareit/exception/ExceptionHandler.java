package ru.practicum.shareit.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;


@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,
            IllegalArgumentException.class,
            ValidationBookingException.class,
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final Exception e) {
        return Map.of("error", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler()
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConversionFailedException(ConversionFailedException e) {
        return Map.of("error", "Unknown state: " + Objects.requireNonNull(e.getValue()));
    }
}
