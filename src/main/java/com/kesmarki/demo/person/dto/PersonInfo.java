package com.kesmarki.demo.person.dto;

import com.kesmarki.demo.address.dto.AddressInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PersonInfo {

    private Integer id;

    private String firstName;

    private String secondName;

    private List<AddressInfo> addresses;
}
