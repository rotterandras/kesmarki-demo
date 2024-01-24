package com.kesmarki.demo.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesmarki.demo.contact.dto.ContactView;
import com.kesmarki.demo.contact.dto.CreateContact;
import com.kesmarki.demo.contact.dto.UpdateContact;
import com.kesmarki.demo.exception.ValidationError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ContactControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    ObjectMapper objectMapper;

    Contact contact = new Contact(1, "1-234-234", 1);
    ContactView contactView = new ContactView(1, "1-234-234", 1);
    CreateContact createContact = new CreateContact(1, "1-234-234", 1);
    UpdateContact updateContact = new UpdateContact("+36-20-456-78-91", 1);
    ContactView updatedContactView = new ContactView(1, "+36-20-456-78-91", 1);

    @Test
    void SaveFindAllSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createContact)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));

        assertThat(contactRepository.findAll())
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(contact);

        mockMvc.perform(get("/api/nyilvantarto/contact"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(contactView))));
    }

    @Test
    void saveFindByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createContact)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));

        assertThat(contactRepository.findById(1)).isEqualTo(Optional.of(contact));

        mockMvc.perform(get("/api/nyilvantarto/contact/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));
    }

    @Test
    void saveNotSuccessful() throws Exception {
        CreateContact createContactWrong = new CreateContact(1, "1-234-234", null);
        ValidationError error = new ValidationError("addressId", "must not be null");

        mockMvc.perform(post("/api/nyilvantarto/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createContactWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(error))));

    }

    @Test
    void saveUpdateFindByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createContact)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));

        assertThat(contactRepository.findById(1)).isEqualTo(Optional.of(contact));

        mockMvc.perform(put("/api/nyilvantarto/contact/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateContact)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedContactView)));

        mockMvc.perform(get("/api/nyilvantarto/contact/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedContactView)));
    }

    @Test
    void updateNotSuccessfulNotFound() throws Exception {
        ValidationError error = new ValidationError("id", "Nem található elérhetőség a megadott ID-val!");
        mockMvc.perform(put("/api/nyilvantarto/contact/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateContact)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    @Test
    void updateNotSuccessfulDataViolation() throws Exception {
        UpdateContact updateContactWrong = new UpdateContact("+36-20-456-78-91", null);
        ValidationError error = new ValidationError("addressId", "must not be null");

        mockMvc.perform(put("/api/nyilvantarto/contact/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateContactWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(error))));
    }

    @Test
    void saveDeleteByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createContact)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));

        assertThat(contactRepository.findById(1)).isEqualTo(Optional.of(contact));

        mockMvc.perform(delete("/api/nyilvantarto/contact/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist()); //empty body

        assertThat(contactRepository.findAll())
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }


    @Test
    void deleteByIdNotSuccessfulNotFound() throws Exception {
        ValidationError error = new ValidationError("id", "Nem található elérhetőség a megadott ID-val!");
        mockMvc.perform(delete("/api/nyilvantarto/contact/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }
}