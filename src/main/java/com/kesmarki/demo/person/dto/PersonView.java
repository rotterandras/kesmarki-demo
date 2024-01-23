package com.kesmarki.demo.person.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonView {

    private Integer id;

    private String firstName;

    private String secondName;
}
