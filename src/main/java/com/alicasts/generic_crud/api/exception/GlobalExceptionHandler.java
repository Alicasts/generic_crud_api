package com.alicasts.generic_crud.api.exception;

import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import com.alicasts.generic_crud.service.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldError(err.getField(), err.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("VALIDATION_ERROR", "Validation failed", fields));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ResourceConflictException ex) {
        List<FieldError> errors = ex.getFields().isEmpty()
                ? List.of()
                : ex.getFields().stream()
                .map(f -> new FieldError(f, "already exists"))
                .toList();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("RESOURCE_CONFLICT", "conflict", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        List<FieldError> errors = new ArrayList<>();

        if (root instanceof InvalidFormatException ife) {
            String field = ife.getPath().isEmpty() ? null : ife.getPath().getFirst().getFieldName();
            String msg = "invalid value";
            Class<?> target = ife.getTargetType();
            if (target.isEnum()) {
                String allowed = Arrays.stream(target.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                msg = "must be one of: " + allowed;
            }
            if (field != null) {
                errors.add(new FieldError(field, msg));
            }
        }

        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_BODY", "invalid request body", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex) {
        List<FieldError> errors = ex.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    return new FieldError(field, v.getMessage());
                })
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("VALIDATION_ERROR", "Validation failed", errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("RESOURCE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_ERROR", ex.getMessage()));
    }

    public record ApiError(String code, String message, List<FieldError> errors) {
            public ApiError(String code, String message) {
                this(code, message, List.of());
            }

    }

    public record FieldError(String field, String message) {
    }
}
