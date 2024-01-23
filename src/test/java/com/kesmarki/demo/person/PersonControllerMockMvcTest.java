package com.kesmarki.demo.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesmarki.demo.exception.PersonNotFoundException;
import com.kesmarki.demo.exception.ValidationError;
import com.kesmarki.demo.person.dto.*;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PersonController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PersonControllerMockMvcTest {

    @MockBean
    PersonService serviceMock;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    PersonView kovacsView = new PersonView(1, "Ákos", "Kovács");
    PersonInfo kovacsInfo = new PersonInfo(1, "Ákos", "Kovács", Collections.emptyList());
    CreatePerson createKovacs = new CreatePerson(1, "Ákos", "Kovács");
    UpdatePerson updateKovacs = new UpdatePerson("Ákos", "Kovács");


    @ParameterizedTest
    @MethodSource("findAllSuccessfulArguments")
    void findAllSuccessful(List<PersonView> serviceReturn) throws Exception {
        when(serviceMock.findAll(new SearchPerson())).thenReturn(serviceReturn);

        mockMvc.perform(get("/api/nyilvantarto/person"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(serviceReturn)));

        verify(serviceMock, times(1)).findAll(any());
        verifyNoMoreInteractions(serviceMock);
    }

    static Stream<Arguments> findAllSuccessfulArguments() {
        PersonView kovacsView = new PersonView(1, "Ákos", "Kovács");
        PersonView lantosView = new PersonView(2, "Piroska", "Lantos");
        return Stream.of(
                Arguments.arguments(List.of()),
                Arguments.arguments(List.of(kovacsView, lantosView))
        );
    }

    @ParameterizedTest
    @MethodSource("findAllViolatedArguments")
    void findAllViolated(String url, List<ValidationError> response) throws Exception {
        mockMvc.perform(get("/api/nyilvantarto/person?" + url))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

    }

    static Stream<Arguments> findAllViolatedArguments() {
        ValidationError firstName = new ValidationError("firstName", "Name must not be blank or empty");
        ValidationError secondName = new ValidationError("secondName", "Name must not be blank or empty");

        return Stream.of(
                Arguments.arguments("firstName=     ", List.of(firstName)),
                Arguments.arguments("secondName=", List.of(secondName)),
                Arguments.arguments("firstName=&secondName=   ", List.of(firstName, secondName))
        );
    }

    @Test
    void findByIdSuccessful() throws Exception {
        when(serviceMock.findById(anyInt())).thenReturn(kovacsInfo);
        mockMvc.perform(get("/api/nyilvantarto/person/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsInfo)));

        verify(serviceMock, times(1)).findById(1);
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void findByIdPersonNotFound() throws Exception {
        ValidationError response = new ValidationError("id", "Nem található személy a megadott ID-val!");

        when(serviceMock.findById(anyInt())).thenThrow(new PersonNotFoundException());
        mockMvc.perform(get("/api/nyilvantarto/person/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(serviceMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void findById_miss_type_cant_convert() throws Exception {
        ValidationError response = new ValidationError("id", "Hibás adattípus");
        mockMvc.perform(get("/api/nyilvantarto/person/wrong-number"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void saveSuccessful() throws Exception {
        when(serviceMock.save(any())).thenReturn(kovacsView);

        mockMvc.perform(post("/api/nyilvantarto/person")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createKovacs)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsView)));

        verify(serviceMock, times(1)).save(any());
        verifyNoMoreInteractions(serviceMock);
    }

    @ParameterizedTest
    @MethodSource("saveViolatedArguments")
    void saveViolated(Integer id, String firstName, String secondName, List<ValidationError> error) throws Exception {
        CreatePerson createPerson = new CreatePerson(id, firstName, secondName);

        mockMvc.perform(post("/api/nyilvantarto/person")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createPerson)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    static Stream<Arguments> saveViolatedArguments() {
        ValidationError firstName = new ValidationError("firstName", "Name must not be blank or empty");
        ValidationError secondName = new ValidationError("secondName", "Name must not be blank or empty");

        return Stream.of(
                Arguments.arguments(1, "", "Kovács", List.of(firstName)),
                Arguments.arguments(1, "Lajos", "    ", List.of(secondName)),
                Arguments.arguments(1, "    ", "", List.of(firstName, secondName))
        );
    }
    @Test
    void updateSuccessful() throws Exception {
        when(serviceMock.update(anyInt(), any())).thenReturn(kovacsView);

        mockMvc.perform(put("/api/nyilvantarto/person/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateKovacs)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(kovacsView)));

        verify(serviceMock, times(1)).update(anyInt(), any());
        verifyNoMoreInteractions(serviceMock);
    }

    @ParameterizedTest
    @MethodSource("updateViolatedArguments")
    void updateViolated(String firstName, String secondName, List<ValidationError> error) throws Exception {
        UpdatePerson updatePerson = new UpdatePerson(firstName, secondName);

        mockMvc.perform(put("/api/nyilvantarto/person/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updatePerson)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    static Stream<Arguments> updateViolatedArguments() {
        ValidationError firstName = new ValidationError("firstName", "Name must not be blank or empty");
        ValidationError secondName = new ValidationError("secondName", "Name must not be blank or empty");

        return Stream.of(
                Arguments.arguments("", "Kovács", List.of(firstName)),
                Arguments.arguments("Lajos", "    ", List.of(secondName)),
                Arguments.arguments("    ", "", List.of(firstName, secondName))
        );
    }


    @Test
    void deleteByIdSuccessful() throws Exception {
        doNothing().when(serviceMock).deleteById(anyInt());
        mockMvc.perform(delete("/api/nyilvantarto/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist()); //empty body

        verify(serviceMock, times(1)).deleteById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }
    @Test
    void deleteByIdNotSuccessfulPersonNotFound() throws Exception {
        ValidationError response = new ValidationError("id", "Nem található személy a megadott ID-val!");

        doThrow(new PersonNotFoundException()).when(serviceMock).deleteById(anyInt());
        mockMvc.perform(delete("/api/nyilvantarto/person/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(serviceMock, times(1)).deleteById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void deleteById_invalid_id() throws Exception {
        ValidationError response = new ValidationError("id", "must be greater than or equal to 0");

        mockMvc.perform(delete("/api/nyilvantarto/person/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }
}