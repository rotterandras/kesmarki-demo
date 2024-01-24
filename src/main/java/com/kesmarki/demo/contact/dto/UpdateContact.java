package com.kesmarki.demo.contact.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContact {

    private String value;

    @NotNull
    private Integer addressId;
}
