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
    private ContactService contactService;
    @Mock
    private ContactRepository contactRepositoryMock;

    Contact contact = new Contact(1, "1-234-234", 1);
    ContactView contactView = new ContactView(1, "1-234-234", 1);
    UpdateContact updateContact = new UpdateContact("+36-20-456-78-91", 1);
    ContactView updatedContactView = new ContactView(1, "+36-20-456-78-91", 1);

    @BeforeEach
    void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        contactService.setModelMapper(modelMapper);
    }

    @Test
    void findAllEmptyList() {
        when(contactRepositoryMock.findAll())
                .thenReturn(Collections.emptyList());
        List<ContactView> result = contactService.findAll();
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .isEmpty();

        verify(contactRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void findAllOneElementList() {
        when(contactRepositoryMock.findAll())
                .thenReturn(List.of(contact));
        List<ContactView> result = contactService.findAll();
        assertThat(result)
                .isInstanceOf(ArrayList.class)
                .hasSize(1)
                .containsExactly(contactView);

        verify(contactRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void findByIdSuccessfull() {
        when(contactRepositoryMock.findById(anyInt())).thenReturn(Optional.of(contact));
        assertThat(contactService.findById(1))
                .isInstanceOf(ContactView.class)
                .isEqualTo(contactView);

        verify(contactRepositoryMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void findByIdUnsuccessfull() {
        when(contactRepositoryMock.findById(anyInt())).thenThrow(new AddressNotFoundException());
        assertThrows(AddressNotFoundException.class, () -> contactService.findById(anyInt()));

        verify(contactRepositoryMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void saveSuccessful() {
        when(contactRepositoryMock.save(any())).thenReturn(contact);
        assertThat(contactService.save(new CreateContact()))
                .isInstanceOf(ContactView.class)
                .isEqualTo(contactView);

        verify(contactRepositoryMock, times(1)).save(any());
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void saveNotSuccessful() {
        when(contactRepositoryMock.save(any())).thenThrow(new DataIntegrityViolationException(""));
        assertThrows(SaveNotSuccessfulException.class, () -> contactService.save(new CreateContact()));

        verify(contactRepositoryMock, times(1)).save(any());
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void updateSuccessful() {
        when(contactRepositoryMock.findById(anyInt())).thenReturn(Optional.of(contact));
        doNothing().when(contactRepositoryMock).flush();
        assertThat(contactService.update(1, updateContact))
                .isInstanceOf(ContactView.class)
                .isEqualTo(updatedContactView);

        verify(contactRepositoryMock, times(1)).findById(anyInt());
        verify(contactRepositoryMock, times(1)).flush();
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void updateNotSuccessfulInvalidData() {
        when(contactRepositoryMock.findById(anyInt())).thenReturn(Optional.of(contact));
        doThrow(new DataIntegrityViolationException("")).when(contactRepositoryMock).flush();
        assertThrows(SaveNotSuccessfulException.class, () -> contactService.update(1, new UpdateContact()));

        verify(contactRepositoryMock, times(1)).findById(anyInt());
        verify(contactRepositoryMock, times(1)).flush();
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void updateNotSuccessfulEntityNotFound() {
        when(contactRepositoryMock.findById(anyInt())).thenThrow(new AddressNotFoundException());
        assertThrows(AddressNotFoundException.class, () -> contactService.update(1, new UpdateContact()));

        verify(contactRepositoryMock, times(1)).findById(anyInt());
        verify(contactRepositoryMock, times(0)).flush();
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void deleteByIdSuccessful() {
        when(contactRepositoryMock.findById(anyInt())).thenReturn(Optional.of(contact));
        doNothing().when(contactRepositoryMock).delete(any());

        contactService.deleteById(1);

        verify(contactRepositoryMock, times(1)).delete(any());
        verifyNoMoreInteractions(contactRepositoryMock);
    }

    @Test
    void deleteByIdNotSuccessfulEntityNotFound() {
        when(contactRepositoryMock.findById(anyInt())).thenThrow(new ContactNotFoundException());
        assertThrows(ContactNotFoundException.class, () -> contactService.deleteById(1));

        verify(contactRepositoryMock, times(1)).findById(anyInt());
        verifyNoMoreInteractions(contactRepositoryMock);
    }
}