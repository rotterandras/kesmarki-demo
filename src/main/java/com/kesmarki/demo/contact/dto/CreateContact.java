package com.kesmarki.demo.contact.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateContact {

    @NotNull
    private Integer id;

    private String value;

    @NotNull
    private Integer addressId;
}
