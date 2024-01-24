package com.kesmarki.demo.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesmarki.demo.address.AddressRepository;
import com.kesmarki.demo.contact.ContactRepository;
import com.kesmarki.demo.exception.ValidationError;
import com.kesmarki.demo.person.dto.CreatePerson;
import com.kesmarki.demo.person.dto.PersonInfo;
import com.kesmarki.demo.person.dto.PersonView;
import com.kesmarki.demo.person.dto.UpdatePerson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PersonControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ObjectMapper objectMapper;

    Person kovacs = new Person(1, "Ákos", "Kovács");
    PersonView kovacsView = new PersonView(1, "Ákos", "Kovács");
    PersonInfo kovacsInfo = new PersonInfo(1, "Ákos", "Kovács", Collections.emptyList());
    CreatePerson createKovacs = new CreatePerson(1, "Ákos", "Kovács");
    UpdatePerson updateKovacs = new UpdatePerson("Ákos", "Asztalos");
    PersonView updatedKovacsView = new PersonView(1, "Ákos", "Asztalos");
    PersonInfo updatedKovacsInfo = new PersonInfo(1, "Ákos", "Asztalos", Collections.emptyList());

    @Test
    void SaveFindAllSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/person")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createKovacs)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsView)));

        assertThat(personRepository.findAll())
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(kovacs);

        mockMvc.perform(get("/api/nyilvantarto/person"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(kovacsView))));
    }

    @Test
    void saveFindByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/person")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createKovacs)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsView)));

        assertThat(personRepository.findById(1)).isEqualTo(Optional.of(kovacs));

        mockMvc.perform(get("/api/nyilvantarto/person/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsInfo)));
    }

    @Test
    void saveNotSuccessful() throws Exception {
        CreatePerson createKovacsWrong = new CreatePerson(1, "    ", "");
        ValidationError firstName = new ValidationError("firstName", "Name must not be blank or empty");
        ValidationError secondName = new ValidationError("secondName", "Name must not be blank or empty");

        mockMvc.perform(post("/api/nyilvantarto/person")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createKovacsWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(firstName, secondName))));

    }

    @Test
    void saveUpdateFindByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/person")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createKovacs)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsView)));

        assertThat(personRepository.findById(1)).isEqualTo(Optional.of(kovacs));

        mockMvc.perform(put("/api/nyilvantarto/person/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateKovacs)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedKovacsView)));

        mockMvc.perform(get("/api/nyilvantarto/person/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedKovacsInfo)));
    }

    @Test
    void updateNotSuccessfulNotFound() throws Exception {
       ValidationError error = new ValidationError("id", "Nem található személy a megadott ID-val!");
        mockMvc.perform(put("/api/nyilvantarto/person/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateKovacs)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    @Test
    void updateNotSuccessfulDataViolation() throws Exception {
        UpdatePerson updateKovacsWrong = new UpdatePerson("    ", "");
        ValidationError firstName = new ValidationError("firstName", "Name must not be blank or empty");
        ValidationError secondName = new ValidationError("secondName", "Name must not be blank or empty");

        mockMvc.perform(put("/api/nyilvantarto/person/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateKovacsWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(firstName, secondName))));
    }

    @Test
    void saveDeleteByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/person")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createKovacs)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsView)));

        assertThat(personRepository.findById(1)).isEqualTo(Optional.of(kovacs));

        mockMvc.perform(delete("/api/nyilvantarto/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist()); //empty body

        assertThat(personRepository.findAll())
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }


    @Test
    void deleteByIdNotSuccessfulNotFound() throws Exception {
        ValidationError error = new ValidationError("id", "Nem található személy a megadott ID-val!");
        mockMvc.perform(delete("/api/nyilvantarto/person/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }
}