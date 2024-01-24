package com.kesmarki.demo.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesmarki.demo.contact.dto.ContactView;
import com.kesmarki.demo.contact.dto.CreateContact;
import com.kesmarki.demo.contact.dto.UpdateContact;
import com.kesmarki.demo.exception.AddressNotFoundException;
import com.kesmarki.demo.exception.ContactNotFoundException;
import com.kesmarki.demo.exception.ValidationError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ContactController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ContactControllerMockMvcTest {

    @MockBean
    ContactService serviceMock;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    ContactView contactView = new ContactView(1, "1-234-234", 1);
    CreateContact createContact = new CreateContact(1, "1-234-234", 1);
    UpdateContact updateContact = new UpdateContact("+36-20-456-78-91", 1);


    @ParameterizedTest
    @MethodSource("findAllSuccessfulArguments")
    void findAllSuccessful(List<ContactView> serviceReturn) throws Exception {
        when(serviceMock.findAll()).thenReturn(serviceReturn);

        mockMvc.perform(get("/api/nyilvantarto/contact"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(serviceReturn)));

        verify(serviceMock, times(1)).findAll();
        verifyNoMoreInteractions(serviceMock);
    }

    static Stream<Arguments> findAllSuccessfulArguments() {
        ContactView contactView = new ContactView(1, "1-234-234", 1);
        ContactView contactView2 = new ContactView(1, "06502345768", 1);
        return Stream.of(
                Arguments.arguments(List.of()),
                Arguments.arguments(List.of(contactView, contactView2))
        );
    }

    @Test
    void findByIdSuccessful() throws Exception {
        when(serviceMock.findById(anyInt())).thenReturn(contactView);
        mockMvc.perform(get("/api/nyilvantarto/contact/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));

        verify(serviceMock, times(1)).findById(1);
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void findByIdContactNotFound() throws Exception {
        ValidationError response = new ValidationError("id", "Nem található elérhetőség a megadott ID-val!");

        when(serviceMock.findById(anyInt())).thenThrow(new ContactNotFoundException());
        mockMvc.perform(get("/api/nyilvantarto/contact/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(serviceMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void findById_miss_type_cant_convert() throws Exception {
        ValidationError response = new ValidationError("id", "Hibás adattípus");
        mockMvc.perform(get("/api/nyilvantarto/contact/wrong-number"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void saveSuccessful() throws Exception {
        when(serviceMock.save(any())).thenReturn(contactView);

        mockMvc.perform(post("/api/nyilvantarto/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createContact)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));

        verify(serviceMock, times(1)).save(any());
        verifyNoMoreInteractions(serviceMock);
    }

    @ParameterizedTest
    @MethodSource("saveAndUpdateViolatedArguments")
    void saveViolated(Integer addressId, List<ValidationError> error) throws Exception {
        CreateContact createAddressWrong = new CreateContact(1, "20-56-78-65-4", addressId);

        mockMvc.perform(post("/api/nyilvantarto/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddressWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    static Stream<Arguments> saveAndUpdateViolatedArguments() {
        ValidationError addressIdError = new ValidationError("addressId", "must not be null");

        return Stream.of(
                Arguments.arguments(null, List.of(addressIdError))
        );
    }

    @Test
    void updateSuccessful() throws Exception {
        when(serviceMock.update(anyInt(), any())).thenReturn(contactView);

        mockMvc.perform(put("/api/nyilvantarto/contact/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateContact)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(contactView)));

        verify(serviceMock, times(1)).update(anyInt(), any());
        verifyNoMoreInteractions(serviceMock);
    }

    @ParameterizedTest
    @MethodSource("saveAndUpdateViolatedArguments")
    void updateViolated(Integer addressId, List<ValidationError> error) throws Exception {
        UpdateContact updateContactWrong = new UpdateContact("20-56-78-65-4", addressId);

        mockMvc.perform(put("/api/nyilvantarto/contact/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateContactWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    @Test
    void deleteByIdSuccessful() throws Exception {
        doNothing().when(serviceMock).deleteById(anyInt());
        mockMvc.perform(delete("/api/nyilvantarto/contact/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist()); //empty body

        verify(serviceMock, times(1)).deleteById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void deleteByIdNotSuccessfulContactNotFound() throws Exception {
        ValidationError response = new ValidationError("id", "Nem található elérhetőség a megadott ID-val!");

        doThrow(new ContactNotFoundException()).when(serviceMock).deleteById(anyInt());
        mockMvc.perform(delete("/api/nyilvantarto/contact/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(serviceMock, times(1)).deleteById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void deleteById_invalid_id() throws Exception {
        ValidationError response = new ValidationError("id", "must be greater than or equal to 0");

        mockMvc.perform(delete("/api/nyilvantarto/contact/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }
}