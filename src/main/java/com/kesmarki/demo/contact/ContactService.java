package com.kesmarki.demo.contact;

import com.kesmarki.demo.address.Address;
import com.kesmarki.demo.address.AddressRepository;
import com.kesmarki.demo.contact.dto.ContactView;
import com.kesmarki.demo.contact.dto.CreateContact;
import com.kesmarki.demo.contact.dto.UpdateContact;
import com.kesmarki.demo.exception.ContactNotFoundException;
import com.kesmarki.demo.exception.PersonNotFoundException;
import com.kesmarki.demo.exception.SaveNotSuccessfulException;
import com.kesmarki.demo.person.Person;
import com.kesmarki.demo.person.PersonRepository;
import com.kesmarki.demo.person.dto.CreatePerson;
import com.kesmarki.demo.person.dto.PersonView;
import com.kesmarki.demo.person.dto.SearchPerson;
import com.kesmarki.demo.person.dto.UpdatePerson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class ContactService {

    private final ModelMapper modelMapper;
    private final ContactRepository contactRepository;

    public List<ContactView> findAll() {
        Type targetLisType = new TypeToken<List<PersonView>>() {
        }.getType();
        List<Contact> contacts = contactRepository.findAll();
        return modelMapper.map(contacts, targetLisType);
    }

    public ContactView findById(Integer id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(ContactNotFoundException::new);

        return modelMapper.map(contact, ContactView.class);
    }

    public ContactView save(CreateContact data) {
        Contact toSave = modelMapper.map(data, Contact.class);
        try {
            Contact saved = contactRepository.save(toSave);
            return modelMapper.map(saved, ContactView.class);
        } catch (DataIntegrityViolationException exception) {
            log.error("Save not successful: " + exception);
            throw new SaveNotSuccessfulException();
        }
    }

    public ContactView update(int id, UpdateContact command) {
        Contact toUpdate = contactRepository.findById(id)
                .orElseThrow(ContactNotFoundException::new);
        Contact data = modelMapper.map(command, Contact.class);
        data.setId(toUpdate.getId());
        modelMapper.map(data, toUpdate);
        try {
            contactRepository.flush();
            return modelMapper.map(toUpdate, ContactView.class);
        } catch (DataIntegrityViolationException exception) {
            log.error("Save not successful: " + exception);
            throw new SaveNotSuccessfulException();
        }
    }

    public void deleteById(int id) {
        Contact toDelete = contactRepository.findById(id)
                .orElseThrow(ContactNotFoundException::new);

        contactRepository.delete(toDelete);
    }

}
