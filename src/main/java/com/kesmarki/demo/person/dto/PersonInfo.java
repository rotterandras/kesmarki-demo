package com.kesmarki.demo.person.dto;

import com.kesmarki.demo.address.dto.AddressInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {

    private Integer id;

    private String firstName;

    private String secondName;

    private List<AddressInfo> addresses;
}
