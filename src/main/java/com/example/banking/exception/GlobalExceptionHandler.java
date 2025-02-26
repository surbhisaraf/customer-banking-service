package com.example.banking.exception;

import com.example.banking.payload.response.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new GenericResponse<>("Not Found ", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<GenericResponse<String>> handleInvalidTransactionException(InvalidTransactionException ex) {
        return new ResponseEntity<>(new GenericResponse<>("Invalid Transaction", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        List<String> errors = bindingResult.getAllErrors().stream()
                .map(objectError -> {
                    if (objectError instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return objectError.getDefaultMessage();
                })
                .toList();

        return new ResponseEntity<>(new GenericResponse<>("Validation failed:", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GenericResponse<String>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(new GenericResponse<>("Invalid username or password"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GenericResponse<String>> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>(new GenericResponse<>("Authentication failed:", ex.getMessage()), HttpStatus.UNAUTHORIZED);

    }
}
