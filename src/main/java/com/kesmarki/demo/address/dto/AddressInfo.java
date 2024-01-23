package com.kesmarki.demo.address.dto;

import com.kesmarki.demo.address.AddressType;
import com.kesmarki.demo.contact.dto.ContactView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AddressInfo {

    private Integer id;

    private AddressType type;

    private String city;

    private String street;

    private List<ContactView> contacts;
}
