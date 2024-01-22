package com.kesmarki.demo.person.dto;

import com.kesmarki.demo.validation.Name;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePerson {

    @Name
    private String firstName;

    @Name
    private String secondName;
}
