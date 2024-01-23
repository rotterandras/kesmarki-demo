package com.kesmarki.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {

    public ValidationError(String message) {
        this.message = message;
    }

    private String field;
    private String message;
}
