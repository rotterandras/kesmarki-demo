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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationError handlePersonNotFound(PersonNotFoundException exception) {
        log.info(exception.toString());
        return new ValidationError("id", "Nem található személy a megadott ID-val!");
    }

    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationError handleContactNotFound(ContactNotFoundException exception) {
        log.info(exception.toString());
        return new ValidationError("id", "Nem található elérhetőség a megadott ID-val!");
    }

    @ExceptionHandler(AddressNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ValidationError handleAddressNotFound(AddressNotFoundException exception) {
        log.info(exception.toString());
        return new ValidationError("id", "Nem található cím a megadott ID-val!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleBadBodyArgumentException(MethodArgumentNotValidException exp) {
        log.info("Handle method argument not valid exception: {}", exp.toString());
        return exp.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationError handleBadTypeExceptions(MethodArgumentTypeMismatchException mate) {
        log.info("Handle type mismatch exception: {}", mate.toString());
        String field = mate.getParameter().getParameterName();
        return new ValidationError(field, "Hibás adattípus");
    }
}
