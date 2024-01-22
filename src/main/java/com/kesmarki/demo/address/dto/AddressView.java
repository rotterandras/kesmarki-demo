package com.kesmarki.demo.address.dto;

import com.kesmarki.demo.address.AddressType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressView {

    private Integer id;

    private AddressType type;

    private String city;

    private String street;

    private Integer personId;
}
