package com.kesmarki.demo.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactView {

    private Integer id;

    private String value;

    private Integer addressId;
}
