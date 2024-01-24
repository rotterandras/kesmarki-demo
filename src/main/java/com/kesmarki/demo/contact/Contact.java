package com.kesmarki.demo.contact;

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
@Table(name = "CONTACT")
public class Contact {

    @Id
    private Integer id;

    @Column(name = "CONTACT_VALUE")
    private String value;

    @Column(name = "ADDRESS_ID")
    private Integer addressId;
}
