package com.kesmarki.demo.person;

import com.kesmarki.demo.address.AddressRepository;
import com.kesmarki.demo.address.AddressService;
import com.kesmarki.demo.contact.ContactRepository;
import com.kesmarki.demo.exception.PersonNotFoundException;
import com.kesmarki.demo.exception.SaveNotSuccessfulException;
import com.kesmarki.demo.person.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class PersonServiceTest {

    private ModelMapper modelMapper = new ModelMapper();
    @Mock
    private AddressService addressService;
    @Mock
    private PersonRepository personRepositoryMock;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private PersonService personService;

    private final Person AKOS_KOVACS = new Person(1, "Ákos", "Kovács");
    private final PersonView AKOS_KOVACS_VIEW = new PersonView(1, "Ákos", "Kovács");
    private final PersonInfo AKOS_KOVACS_INFO = new PersonInfo(1, "Ákos", "Kovács", Collections.emptyList());

    @BeforeEach
    void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        personService.setModelMapper(modelMapper);
    }

    @Test
    @DisplayName("FindAll() empty list")
    void findAllEmptyList() {
        Example<Person> example = Example.of(new Person());
        when(personRepositoryMock.findAll(example))
                .thenReturn(Collections.emptyList());
        List<PersonView> result = personService.findAll(new SearchPerson());
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .isEmpty();

        verify(personRepositoryMock, times(1)).findAll(example);
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("FindAll() one element list")
    void findAllOneElementList() {
        Example<Person> example = Example.of(new Person());
        when(personRepositoryMock.findAll(example))
                .thenReturn(List.of(AKOS_KOVACS));
        List<PersonView> result = personService.findAll(new SearchPerson());
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(AKOS_KOVACS_VIEW);

        verify(personRepositoryMock, times(1)).findAll(example);
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("FindById() successful")
    void findByIdSuccessfull() {
        when(personRepositoryMock.findById(anyInt())).thenReturn(Optional.of(AKOS_KOVACS));
        when(addressService.findAllByPersonId(anyInt())).thenReturn(Collections.emptyList());
        assertThat(personService.findById(1))
                .isInstanceOf(PersonInfo.class)
                .isEqualTo(AKOS_KOVACS_INFO);

        verify(personRepositoryMock, times(1)).findById(anyInt());
        verify(addressService, times(1)).findAllByPersonId(anyInt());
        verifyNoMoreInteractions(personRepositoryMock);
        verifyNoMoreInteractions(addressService);
    }

    @Test
    @DisplayName("FindById() unsuccessful")
    void findByIdUnsuccessfull() {
        when(personRepositoryMock.findById(anyInt())).thenThrow(new PersonNotFoundException());
        assertThrows(PersonNotFoundException.class, () -> personService.findById(anyInt()));

        verify(personRepositoryMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("Save() successful")
    void saveSuccessful() {
        when(personRepositoryMock.save(any())).thenReturn(AKOS_KOVACS);
        assertThat(personService.save(new CreatePerson()))
                .isInstanceOf(PersonView.class)
                .isEqualTo(AKOS_KOVACS_VIEW);

        verify(personRepositoryMock, times(1)).save(any());
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("Save() not successful")
    void saveNotSuccessful() {
        when(personRepositoryMock.save(any())).thenThrow(new DataIntegrityViolationException(""));
        assertThrows(SaveNotSuccessfulException.class, () -> personService.save(new CreatePerson()));

        verify(personRepositoryMock, times(1)).save(any());
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("Update() successful")
    void updateSuccessful() {
        PersonView result = new PersonView(1, "Ádám", "Kovács");
        UpdatePerson update = new UpdatePerson("Ádám", "Kovács");
        when(personRepositoryMock.findById(anyInt())).thenReturn(Optional.of(AKOS_KOVACS));
        doNothing().when(personRepositoryMock).flush();
        assertThat(personService.update(1, update))
                .isInstanceOf(PersonView.class)
                .isEqualTo(result);

        verify(personRepositoryMock, times(1)).findById(anyInt());
        verify(personRepositoryMock, times(1)).flush();
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("Update() not successful due to invalid data")
    void updateNotSuccessfulInvalidData() {
        when(personRepositoryMock.findById(anyInt())).thenReturn(Optional.of(AKOS_KOVACS));
        doThrow(new DataIntegrityViolationException("")).when(personRepositoryMock).flush();
        assertThrows(SaveNotSuccessfulException.class, () -> personService.update(1, new UpdatePerson()));

        verify(personRepositoryMock, times(1)).findById(anyInt());
        verify(personRepositoryMock, times(1)).flush();
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("Update() not successful due to entity not found")
    void updateNotSuccessfulEntityNotFound() {
        when(personRepositoryMock.findById(anyInt())).thenThrow(new PersonNotFoundException());
        assertThrows(PersonNotFoundException.class, () -> personService.update(1, new UpdatePerson()));

        verify(personRepositoryMock, times(1)).findById(anyInt());
        verify(personRepositoryMock, times(0)).flush();
        verifyNoMoreInteractions(personRepositoryMock);
    }

    @Test
    @DisplayName("DeleteById() successful")
    void deleteByIdSuccessful() {
        when(personRepositoryMock.findById(anyInt())).thenReturn(Optional.of(AKOS_KOVACS));
        when(addressRepository.findAllByPersonIdIn(anyList())).thenReturn(Collections.emptyList());
        when(contactRepository.findAllByAddressIdIn(anyList())).thenReturn(Collections.emptyList());
        doNothing().when(contactRepository).deleteAll(any());
        doNothing().when(addressRepository).deleteAllById(any());
        doNothing().when(personRepositoryMock).delete(any());

        personService.deleteById(1);

        verify(personRepositoryMock, times(1)).findById(anyInt());
        verify(addressRepository, times(1)).findAllByPersonIdIn(anyList());
        verify(contactRepository, times(1)).findAllByAddressIdIn(anyList());
        verify(personRepositoryMock, times(1)).delete(any());
        verify(addressRepository, times(1)).deleteAllById(any());
        verify(contactRepository, times(1)).deleteAll(any());
        verifyNoMoreInteractions(personRepositoryMock);
        verifyNoMoreInteractions(addressRepository);
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    @DisplayName("DeleteById() not successful, person not found")
    void deleteByIdNotSuccessfulPersonNotFound() {
        when(personRepositoryMock.findById(anyInt())).thenThrow(new PersonNotFoundException());
        assertThrows(PersonNotFoundException.class, () -> personService.deleteById(1));

        verify(personRepositoryMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(personRepositoryMock);
    }
}