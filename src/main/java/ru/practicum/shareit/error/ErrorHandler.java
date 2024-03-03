package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DataException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MissingRequestHeaderException exception) {

        log.info("Get MissingRequestHeaderException from {}", exception.getParameter());

        return new ErrorResponse("Missing header parameter",
                "Parameter name '" + exception.getHeaderName() + "'");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final IllegalArgumentException exception) {

        log.info("Get IllegalArgumentException, {}", exception.getMessage());

        return new ErrorResponse("Illegal argument",
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException exception) {

        log.info("Get ValidationException: {}", exception.getParameter());

        return new ErrorResponse("Validation failed",
                exception.getParameter().getParameterName());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ConstraintViolationException exception) {

        log.info("Get ValidationException: {}", exception.getMessage());

        return new ErrorResponse("Validation failed",
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final DataException exception) {

        log.info("Get ExistException {}", exception.getMessage());

        return new ErrorResponse("Link error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final DataIntegrityViolationException exception) {

        log.info("Get DataIntegrityViolationException, {}", exception.getMessage());

        return new ErrorResponse("Illegal argument", null);
    }
}