package com.kesmarki.demo.address.dto;

import com.kesmarki.demo.address.AddressType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchAddress {


    private AddressType type;

    private String city;

    private String street;
}
