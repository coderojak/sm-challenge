package sm.challenge.email.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import sm.challenge.email.to.GenericResponse;
import sm.challenge.email.to.GenericResponse.Status;
import sm.challenge.email.util.Exceptions;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleInputNotValid(IllegalArgumentException ex, WebRequest request) {
        return handleExceptionInternal(ex, new GenericResponse(Status.ERROR, "Invalid input: " + ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleUnexpectedException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new GenericResponse(Status.ERROR,
                        "Unexpected exception with root cause: " + Exceptions.getRootCause(ex)),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
