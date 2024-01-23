package com.kesmarki.demo.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationError handleDuplicateAddress(DataIntegrityViolationException exception) {
        log.info(exception.toString());
        return new ValidationError("Egy személynek csak két címe lehet");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleConstraints(ConstraintViolationException cve) {
        log.info("Handle constraint violation: {}", cve.toString());
        return cve.getConstraintViolations().stream()
                .map(v -> {
                            String field = null;
                            for (Path.Node node : v.getPropertyPath()) {
                                field = node.getName();
                            }
                            return new ValidationError(field, v.getMessage());
                        }
                ).collect(Collectors.toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationError handleWrongEnum(HttpMessageNotReadableException exception) {
        log.info(exception.toString());
        return new ValidationError("Hibás adattípus");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleBadBodyArgumentException(MethodArgumentNotValidException exp) {
        log.info("Handle method argument not valid exception: {}", exp.toString());
        return exp.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
    }
}
