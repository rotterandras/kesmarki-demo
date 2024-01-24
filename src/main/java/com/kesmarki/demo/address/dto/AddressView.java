package com.kesmarki.demo.address.dto;

import com.kesmarki.demo.address.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressView {

    private Integer id;

    private AddressType type;

    private String city;

    private String street;

    private Integer personId;
}
