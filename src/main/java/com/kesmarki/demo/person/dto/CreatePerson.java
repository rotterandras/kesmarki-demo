package com.kesmarki.demo.person.dto;

import com.kesmarki.demo.validation.Name;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePerson {

    @NotNull
    private Integer id;

    @Name
    private String firstName;

    @Name
    private String secondName;
}
