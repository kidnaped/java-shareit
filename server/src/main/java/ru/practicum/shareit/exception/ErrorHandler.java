package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.Generated;

@Generated
@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {
    private static final String EXCEPTION_CAUGHT = "Exception caught: ";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn(EXCEPTION_CAUGHT + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.warn(EXCEPTION_CAUGHT + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeExceptions(RuntimeException e) {
        log.warn(EXCEPTION_CAUGHT + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOtherExceptions(Exception e) {
        log.warn(EXCEPTION_CAUGHT + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }


}
