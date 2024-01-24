package com.kesmarki.demo.address;

import com.kesmarki.demo.address.dto.AddressView;
import com.kesmarki.demo.address.dto.CreateAddress;
import com.kesmarki.demo.address.dto.SearchAddress;
import com.kesmarki.demo.address.dto.UpdateAddress;
import com.kesmarki.demo.contact.Contact;
import com.kesmarki.demo.contact.ContactRepository;
import com.kesmarki.demo.exception.AddressNotFoundException;
import com.kesmarki.demo.exception.SaveNotSuccessfulException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    private ModelMapper modelMapper = new ModelMapper();
    @InjectMocks
    private AddressService addressService;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private AddressRepository addressRepository;

    Address address = new Address(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    AddressView addressView = new AddressView(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    CreateAddress createAddress = new CreateAddress(1, AddressType.TEMPORARY, "Pécs", "Hajnal u. 7", 1);
    UpdateAddress updateAddress = new UpdateAddress(AddressType.PERMANENT, "Budapest", "Hajnal u. 7", 1);
    AddressView updatedAddressView = new AddressView(1, AddressType.PERMANENT, "Budapest", "Hajnal u. 7", 1);
    Example<Address> example = Example.of(new Address());

    @BeforeEach
    void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        addressService.setModelMapper(modelMapper);
    }

    @Test
    void findAllEmptyList() {
        when(addressRepository.findAll(example))
                .thenReturn(Collections.emptyList());
        List<AddressView> result = addressService.findAll(new SearchAddress());
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .isEmpty();

        verify(addressRepository, times(1)).findAll(example);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void findAllOneElementList() {
        when(addressRepository.findAll(example))
                .thenReturn(List.of(address));
        List<AddressView> result = addressService.findAll(new SearchAddress());
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(addressView);

        verify(addressRepository, times(1)).findAll(example);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void findByIdSuccessfull() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        assertThat(addressService.findById(1))
                .isInstanceOf(AddressView.class)
                .isEqualTo(addressView);

        verify(addressRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void findByIdUnsuccessfull() {
        when(addressRepository.findById(anyInt())).thenThrow(new AddressNotFoundException());
        assertThrows(AddressNotFoundException.class, () -> addressService.findById(anyInt()));

        verify(addressRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void saveSuccessful() {
        when(addressRepository.save(any())).thenReturn(address);
        assertThat(addressService.save(new CreateAddress()))
                .isInstanceOf(AddressView.class)
                .isEqualTo(addressView);

        verify(addressRepository, times(1)).save(any());
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void saveNotSuccessful() {
        when(addressRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));
        assertThrows(SaveNotSuccessfulException.class, () -> addressService.save(new CreateAddress()));

        verify(addressRepository, times(1)).save(any());
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void updateSuccessful() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).flush();
        assertThat(addressService.update(1, updateAddress))
                .isInstanceOf(AddressView.class)
                .isEqualTo(updatedAddressView);

        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times(1)).flush();
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void updateNotSuccessfulInvalidData() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        doThrow(new DataIntegrityViolationException("")).when(addressRepository).flush();
        assertThrows(SaveNotSuccessfulException.class, () -> addressService.update(1, new UpdateAddress()));

        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times(1)).flush();
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void updateNotSuccessfulEntityNotFound() {
        when(addressRepository.findById(anyInt())).thenThrow(new AddressNotFoundException());
        assertThrows(AddressNotFoundException.class, () -> addressService.update(1, new UpdateAddress()));

        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times(0)).flush();
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void deleteByIdSuccessful() {
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        Example<Contact> exampleContact = Example.of(new Contact(null, null, 1));
        when(contactRepository.findAll(exampleContact)).thenReturn(Collections.emptyList());
        doNothing().when(contactRepository).deleteAll(any());
        doNothing().when(addressRepository).delete(any());

        addressService.deleteById(1);

        verify(addressRepository, times(1)).findById(anyInt());
        verify(contactRepository, times(1)).findAll(exampleContact);
        verify(addressRepository, times(1)).delete(any());
        verify(contactRepository, times(1)).deleteAll(any());
        verifyNoMoreInteractions(addressRepository);
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void deleteByIdNotSuccessfulEntityNotFound() {
        when(addressRepository.findById(anyInt())).thenThrow(new AddressNotFoundException());
        assertThrows(AddressNotFoundException.class, () -> addressService.deleteById(1));

        verify(addressRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(addressRepository);
    }
}