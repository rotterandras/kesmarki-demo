package com.kesmarki.demo.contact.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContactView {

    private Integer id;

    private String value;

    private Integer addressId;
}
