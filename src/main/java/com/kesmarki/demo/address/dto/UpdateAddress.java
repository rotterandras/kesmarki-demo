package com.kesmarki.demo.address.dto;

import com.kesmarki.demo.address.AddressType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddress {

    @NotNull
    private AddressType type;

    private String city;

    private String street;

    @NotNull
    private Integer personId;
}
