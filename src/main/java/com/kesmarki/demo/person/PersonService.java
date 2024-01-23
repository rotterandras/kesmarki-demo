package com.kesmarki.demo.person;

import com.kesmarki.demo.address.Address;
import com.kesmarki.demo.address.AddressRepository;
import com.kesmarki.demo.address.AddressService;
import com.kesmarki.demo.address.dto.AddressInfo;
import com.kesmarki.demo.contact.Contact;
import com.kesmarki.demo.contact.ContactRepository;
import com.kesmarki.demo.exception.PersonNotFoundException;
import com.kesmarki.demo.exception.SaveNotSuccessfulException;
import com.kesmarki.demo.person.dto.*;
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
public class PersonService {

    private final ModelMapper modelMapper;
    private final AddressService addressService;
    private final PersonRepository personRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    public List<PersonView> findAll(SearchPerson searchPerson) {
        Type targetLisType = new TypeToken<List<PersonView>>() {
        }.getType();
        Person examplePerson = modelMapper.map(searchPerson, Person.class);
        List<Person> people = personRepository.findAll(Example.of(examplePerson));
        return modelMapper.map(people, targetLisType);
    }

    public PersonInfo findById(Integer id) {
        Person person = personRepository.findById(id)
                .orElseThrow(PersonNotFoundException::new);

        PersonInfo info = modelMapper.map(person, PersonInfo.class);
        List<AddressInfo> addresses = addressService.findAllByPersonId(person.getId());
        info.setAddresses(addresses);
        return info;
    }

    public PersonView save(CreatePerson data) {
        Person toSave = modelMapper.map(data, Person.class);
        try {
            Person saved = personRepository.save(toSave);
            return modelMapper.map(saved, PersonView.class);
        } catch (DataIntegrityViolationException exception) {
            log.error("Save not successful: " + exception);
            throw new SaveNotSuccessfulException();
        }
    }

    public PersonView update(int id, UpdatePerson command) {
        Person toUpdate = personRepository.findById(id)
                .orElseThrow(PersonNotFoundException::new);
        Person data = modelMapper.map(command, Person.class);
        data.setId(toUpdate.getId());
        modelMapper.map(data, toUpdate);
        try {
            personRepository.flush();
            return modelMapper.map(toUpdate, PersonView.class);
        } catch (DataIntegrityViolationException exception) {
            log.error("Save not successful: " + exception);
            throw new SaveNotSuccessfulException();
        }
    }

    public void deleteById(int id) {
        Person toDelete = personRepository.findById(id)
                .orElseThrow(PersonNotFoundException::new);

        List<Integer> addressIds = addressRepository.findAllByPersonIdIn(List.of(toDelete.getId())).stream()
                .map(Address::getId)
                .toList();

        List<Contact> contacts = contactRepository.findAllByAddressIdIn(addressIds);

        contactRepository.deleteAll(contacts);
        addressRepository.deleteAllById(addressIds);
        personRepository.delete(toDelete);
    }

}
