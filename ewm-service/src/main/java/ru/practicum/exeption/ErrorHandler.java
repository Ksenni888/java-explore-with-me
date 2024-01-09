package ru.practicum.exeption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("Incorrectly made request.");
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", "Field: name. Error: must not be blank. Value: null", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("Integrity constraint has been violated.");
        return new ErrorResponse(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundExeption(final ObjectNotFoundException e) {
        log.warn("The required object was not found.");
        return new ErrorResponse(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage(), LocalDateTime.now());
    }

}