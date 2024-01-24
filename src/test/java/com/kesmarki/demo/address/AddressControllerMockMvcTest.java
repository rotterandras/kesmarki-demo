package com.kesmarki.demo.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kesmarki.demo.address.dto.AddressView;
import com.kesmarki.demo.address.dto.CreateAddress;
import com.kesmarki.demo.address.dto.SearchAddress;
import com.kesmarki.demo.address.dto.UpdateAddress;
import com.kesmarki.demo.exception.AddressNotFoundException;
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

@WebMvcTest(controllers = AddressController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AddressControllerMockMvcTest {

    @MockBean
    AddressService serviceMock;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    AddressView addressView = new AddressView(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    CreateAddress createAddress = new CreateAddress(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    UpdateAddress updateAddress = new UpdateAddress(AddressType.PERMANENT, "Budapest", "Hajnal u. 7", 1);


    @ParameterizedTest
    @MethodSource("findAllSuccessfulArguments")
    void findAllSuccessful(List<AddressView> serviceReturn) throws Exception {
        when(serviceMock.findAll(new SearchAddress())).thenReturn(serviceReturn);

        mockMvc.perform(get("/api/nyilvantarto/address"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(serviceReturn)));

        verify(serviceMock, times(1)).findAll(any());
        verifyNoMoreInteractions(serviceMock);
    }

    static Stream<Arguments> findAllSuccessfulArguments() {
        AddressView addressView = new AddressView(1, AddressType.PERMANENT, "Pécs", "Hajnal u. 7", 1);
        AddressView addressView2 = new AddressView(1, AddressType.TEMPORARY, "Budapest", "Hajnal u. 7", 1);
        return Stream.of(
                Arguments.arguments(List.of()),
                Arguments.arguments(List.of(addressView, addressView2))
        );
    }

    @Test
    void findByIdSuccessful() throws Exception {
        when(serviceMock.findById(anyInt())).thenReturn(addressView);
        mockMvc.perform(get("/api/nyilvantarto/address/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));

        verify(serviceMock, times(1)).findById(1);
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void findByIdAddressNotFound() throws Exception {
        ValidationError response = new ValidationError("id", "Nem található cím a megadott ID-val!");

        when(serviceMock.findById(anyInt())).thenThrow(new AddressNotFoundException());
        mockMvc.perform(get("/api/nyilvantarto/address/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(serviceMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void findById_miss_type_cant_convert() throws Exception {
        ValidationError response = new ValidationError("id", "Hibás adattípus");
        mockMvc.perform(get("/api/nyilvantarto/address/wrong-number"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void saveSuccessful() throws Exception {
        when(serviceMock.save(any())).thenReturn(addressView);

        mockMvc.perform(post("/api/nyilvantarto/address")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddress)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));

        verify(serviceMock, times(1)).save(any());
        verifyNoMoreInteractions(serviceMock);
    }

    @ParameterizedTest
    @MethodSource("saveAndUpdateViolatedArguments")
    void saveViolated(AddressType type, Integer personId, List<ValidationError> error) throws Exception {
        CreateAddress createAddressWrong = new CreateAddress(1, type, "Pécs", "Hajnal u. 7", personId);

        mockMvc.perform(post("/api/nyilvantarto/address")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createAddressWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    static Stream<Arguments> saveAndUpdateViolatedArguments() {
        ValidationError typeError = new ValidationError("type", "must not be null");
        ValidationError personIdError = new ValidationError("personId", "must not be null");

        return Stream.of(
                Arguments.arguments(null, 1, List.of(typeError)),
                Arguments.arguments(AddressType.PERMANENT, null, List.of(personIdError)),
                Arguments.arguments(null, null, List.of(typeError, personIdError))
        );
    }

    @Test
    void updateSuccessful() throws Exception {
        when(serviceMock.update(anyInt(), any())).thenReturn(addressView);

        mockMvc.perform(put("/api/nyilvantarto/address/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateAddress)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(addressView)));

        verify(serviceMock, times(1)).update(anyInt(), any());
        verifyNoMoreInteractions(serviceMock);
    }

    @ParameterizedTest
    @MethodSource("saveAndUpdateViolatedArguments")
    void updateViolated(AddressType type, Integer personId, List<ValidationError> error) throws Exception {
        UpdateAddress updateAddressWrong = new UpdateAddress(type, "Pécs", "Hajnal u. 7", personId);

        mockMvc.perform(put("/api/nyilvantarto/address/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateAddressWrong)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(error)));
    }

    @Test
    void deleteByIdSuccessful() throws Exception {
        doNothing().when(serviceMock).deleteById(anyInt());
        mockMvc.perform(delete("/api/nyilvantarto/address/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist()); //empty body

        verify(serviceMock, times(1)).deleteById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void deleteByIdNotSuccessfulAddressNotFound() throws Exception {
        ValidationError response = new ValidationError("id", "Nem található cím a megadott ID-val!");

        doThrow(new AddressNotFoundException()).when(serviceMock).deleteById(anyInt());
        mockMvc.perform(delete("/api/nyilvantarto/address/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(serviceMock, times(1)).deleteById(anyInt());
        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void deleteById_invalid_id() throws Exception {
        ValidationError response = new ValidationError("id", "must be greater than or equal to 0");

        mockMvc.perform(delete("/api/nyilvantarto/address/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }
}