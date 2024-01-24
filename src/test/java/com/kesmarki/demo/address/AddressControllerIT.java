package com.kesmarki.demo.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesmarki.demo.address.dto.AddressView;
import com.kesmarki.demo.address.dto.CreateAddress;
import com.kesmarki.demo.address.dto.UpdateAddress;
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
class AddressControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ObjectMapper objectMapper;

    Address address = new Address(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    AddressView addressView = new AddressView(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    CreateAddress createAddress = new CreateAddress(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    UpdateAddress updateAddress = new UpdateAddress(AddressType.PERMANENT, "Budapest", "Hajnal u. 7", 1);
    AddressView updatedAddressView = new AddressView(1, AddressType.PERMANENT, "Budapest", "Hajnal u. 7", 1);

    @Test
    void SaveFindAllSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/address")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));

        assertThat(addressRepository.findAll())
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(address);

        mockMvc.perform(get("/api/nyilvantarto/address"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(addressView))));
    }

    @Test
    void saveFindByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/address")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));

        assertThat(addressRepository.findById(1)).isEqualTo(Optional.of(address));

        mockMvc.perform(get("/api/nyilvantarto/address/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));
    }

    @Test
    void saveNotSuccessful() throws Exception {
        CreateAddress createAddressWrong = new CreateAddress(1, null, "Pécs", "Hajnal u. 7", null);
        ValidationError typeError = new ValidationError("type", "must not be null");
        ValidationError personIdError = new ValidationError("personId", "must not be null");

        mockMvc.perform(post("/api/nyilvantarto/address")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddressWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(typeError, personIdError))));

    }

    @Test
    void saveUpdateFindByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/address")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));

        assertThat(addressRepository.findById(1)).isEqualTo(Optional.of(address));

        mockMvc.perform(put("/api/nyilvantarto/address/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateAddress)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedAddressView)));

        mockMvc.perform(get("/api/nyilvantarto/address/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedAddressView)));
    }

    @Test
    void updateNotSuccessfulNotFound() throws Exception {
        ValidationError error = new ValidationError("id", "Nem található cím a megadott ID-val!");
        mockMvc.perform(put("/api/nyilvantarto/address/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateAddress)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    @Test
    void updateNotSuccessfulDataViolation() throws Exception {
        UpdateAddress updateAddressWrong = new UpdateAddress(null, "Pécs", "Hajnal u. 7", null);
        ValidationError typeError = new ValidationError("type", "must not be null");
        ValidationError personIdError = new ValidationError("personId", "must not be null");

        mockMvc.perform(put("/api/nyilvantarto/address/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateAddressWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(typeError, personIdError))));
    }

    @Test
    void saveDeleteByIdSuccessful() throws Exception {
        mockMvc.perform(post("/api/nyilvantarto/address")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));

        assertThat(addressRepository.findById(1)).isEqualTo(Optional.of(address));

        mockMvc.perform(delete("/api/nyilvantarto/address/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist()); //empty body

        assertThat(addressRepository.findAll())
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }


    @Test
    void deleteByIdNotSuccessfulNotFound() throws Exception {
        ValidationError error = new ValidationError("id", "Nem található cím a megadott ID-val!");
        mockMvc.perform(delete("/api/nyilvantarto/address/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }
}