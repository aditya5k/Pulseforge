package com.aditya.pulseforge.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Covers our own 401 / 409 etc. so they use the standard ErrorResponse format
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException ex) {

        int status = ex.getStatusCode().value();

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status,
                ex.getReason(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    // Safety net for races: the DB constraint (e.g. foreign key) is the source of truth.
    // The raw DB message is logged, never sent to the client (it leaks table/column names).
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        log.warn("Data integrity violation", ex);

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                409,
                "Operation conflicts with existing data",
                null
        );

        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                404,
                ex.getMessage(),
                null
        );

        return ResponseEntity
                .status(404)
                .body(response);
    }
}