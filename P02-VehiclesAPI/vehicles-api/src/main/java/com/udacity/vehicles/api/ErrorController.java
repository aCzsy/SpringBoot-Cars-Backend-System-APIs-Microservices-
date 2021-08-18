package com.udacity.vehicles.api;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Implements the Error controller related to any errors handled by the Vehicles API
 */

/**
@ControllerAdvice is a specialization of the @Component annotation which allows
to handle exceptions across the whole application in one global handling component.
It can be viewed as an interceptor of exceptions thrown by methods annotated with @RequestMapping and similar.
ResponseEntityExceptionHandler is a convenient base class for @ControllerAdvice classes
that wish to provide centralized exception handling across all @RequestMapping methods
through @ExceptionHandler methods. It provides an methods for handling internal Spring MVC exceptions.
It returns a ResponseEntity in contrast to DefaultHandlerExceptionResolver which returns a ModelAndView.
 */

/**
 * THIS METHOD VALIDATES FIELDS ANNOTATED WITH @Valid
 */
@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_VALIDATION_FAILED_MESSAGE = "Validation failed";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(
                        Collectors.toList());

        ApiError apiError = new ApiError(DEFAULT_VALIDATION_FAILED_MESSAGE, errors);
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }
}

