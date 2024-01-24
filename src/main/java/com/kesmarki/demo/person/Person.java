package com.kesmarki.demo.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PERSON")
public class Person {

    @Id
    private Integer id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "SECOND_NAME")
    private String secondName;
}
