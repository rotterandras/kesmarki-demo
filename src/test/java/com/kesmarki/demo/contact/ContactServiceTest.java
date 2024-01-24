package com.kesmarki.demo.contact;

import com.kesmarki.demo.contact.dto.ContactView;
import com.kesmarki.demo.contact.dto.CreateContact;
import com.kesmarki.demo.contact.dto.UpdateContact;
import com.kesmarki.demo.exception.AddressNotFoundException;
import com.kesmarki.demo.exception.ContactNotFoundException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    private ModelMapper modelMapper = new ModelMapper();
    @InjectMocks
    private ContactService contactServiceMock;
    @Mock
    private ContactRepository contactRepository;

    Contact contact = new Contact(1, "1-234-234", 1);
    ContactView contactView = new ContactView(1, "1-234-234", 1);
    UpdateContact updateContact = new UpdateContact("+36-20-456-78-91", 1);
    ContactView updatedContactView = new ContactView(1, "+36-20-456-78-91", 1);

    @BeforeEach
    void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        contactServiceMock.setModelMapper(modelMapper);
    }

    @Test
    void findAllEmptyList() {
        when(contactRepository.findAll())
                .thenReturn(Collections.emptyList());
        List<ContactView> result = contactServiceMock.findAll();
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .isEmpty();

        verify(contactRepository, times(1)).findAll();
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void findAllOneElementList() {
        when(contactRepository.findAll())
                .thenReturn(List.of(contact));
        List<ContactView> result = contactServiceMock.findAll();
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(contactView);

        verify(contactRepository, times(1)).findAll();
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void findByIdSuccessfull() {
        when(contactRepository.findById(anyInt())).thenReturn(Optional.of(contact));
        assertThat(contactServiceMock.findById(1))
                .isInstanceOf(ContactView.class)
                .isEqualTo(contactView);

        verify(contactRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void findByIdUnsuccessfull() {
        when(contactRepository.findById(anyInt())).thenThrow(new AddressNotFoundException());
        assertThrows(AddressNotFoundException.class, () -> contactServiceMock.findById(anyInt()));

        verify(contactRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void saveSuccessful() {
        when(contactRepository.save(any())).thenReturn(contact);
        assertThat(contactServiceMock.save(new CreateContact()))
                .isInstanceOf(ContactView.class)
                .isEqualTo(contactView);

        verify(contactRepository, times(1)).save(any());
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void saveNotSuccessful() {
        when(contactRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));
        assertThrows(SaveNotSuccessfulException.class, () -> contactServiceMock.save(new CreateContact()));

        verify(contactRepository, times(1)).save(any());
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void updateSuccessful() {
        when(contactRepository.findById(anyInt())).thenReturn(Optional.of(contact));
        doNothing().when(contactRepository).flush();
        assertThat(contactServiceMock.update(1, updateContact))
                .isInstanceOf(ContactView.class)
                .isEqualTo(updatedContactView);

        verify(contactRepository, times(1)).findById(anyInt());
        verify(contactRepository, times(1)).flush();
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void updateNotSuccessfulInvalidData() {
        when(contactRepository.findById(anyInt())).thenReturn(Optional.of(contact));
        doThrow(new DataIntegrityViolationException("")).when(contactRepository).flush();
        assertThrows(SaveNotSuccessfulException.class, () -> contactServiceMock.update(1, new UpdateContact()));

        verify(contactRepository, times(1)).findById(anyInt());
        verify(contactRepository, times(1)).flush();
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void updateNotSuccessfulEntityNotFound() {
        when(contactRepository.findById(anyInt())).thenThrow(new AddressNotFoundException());
        assertThrows(AddressNotFoundException.class, () -> contactServiceMock.update(1, new UpdateContact()));

        verify(contactRepository, times(1)).findById(anyInt());
        verify(contactRepository, times(0)).flush();
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void deleteByIdSuccessful() {
        when(contactRepository.findById(anyInt())).thenReturn(Optional.of(contact));
        doNothing().when(contactRepository).delete(any());

        contactServiceMock.deleteById(1);

        verify(contactRepository, times(1)).delete(any());
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    void deleteByIdNotSuccessfulEntityNotFound() {
        when(contactRepository.findById(anyInt())).thenThrow(new ContactNotFoundException());
        assertThrows(ContactNotFoundException.class, () -> contactServiceMock.deleteById(1));

        verify(contactRepository, times(1)).findById(anyInt());
        verifyNoMoreInteractions(contactRepository);
    }
}