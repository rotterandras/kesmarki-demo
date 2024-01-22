package com.kesmarki.demo.person.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonView {

    private Integer id;

    private String firstName;

    private String secondName;
}
