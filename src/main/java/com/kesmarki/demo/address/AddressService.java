package com.kesmarki.demo.address;

import com.kesmarki.demo.address.dto.AddressView;
import com.kesmarki.demo.address.dto.CreateAddress;
import com.kesmarki.demo.address.dto.SearchAddress;
import com.kesmarki.demo.address.dto.UpdateAddress;
import com.kesmarki.demo.contact.Contact;
import com.kesmarki.demo.contact.ContactRepository;
import com.kesmarki.demo.exception.AddressNotFoundException;
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
public class AddressService {

    private final ModelMapper modelMapper;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    public List<AddressView> findAll(SearchAddress searchAddress) {
        Type targetLisType = new TypeToken<List<AddressView>>() {
        }.getType();
        Address exampleAddress = modelMapper.map(searchAddress, Address.class);
        List<Address> addresses = addressRepository.findAll(Example.of(exampleAddress));
        return modelMapper.map(addresses, targetLisType);
    }

    public AddressView findById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(AddressNotFoundException::new);

        return modelMapper.map(address, AddressView.class);
    }

    public AddressView save(CreateAddress data) {
        Address toSave = modelMapper.map(data, Address.class);
        try {
            Address saved = addressRepository.save(toSave);
            return modelMapper.map(saved, AddressView.class);
        } catch (DataIntegrityViolationException exception) {
            log.error("Save not successful: " + exception);
            throw new SaveNotSuccessfulException();
        }
    }

    public AddressView update(int id, UpdateAddress command) {
        Address toUpdate = addressRepository.findById(id)
                .orElseThrow(AddressNotFoundException::new);
        Address data = modelMapper.map(command, Address.class);
        data.setId(toUpdate.getId());
        modelMapper.map(data, toUpdate);
        try {
            addressRepository.flush();
            return modelMapper.map(toUpdate, AddressView.class);
        } catch (DataIntegrityViolationException exception) {
            log.error("Save not successful: " + exception);
            throw new SaveNotSuccessfulException();
        }
    }

    public void deleteById(int id) {
        Address toDelete = addressRepository.findById(id)
                .orElseThrow(AddressNotFoundException::new);

        Contact exampleContact = new Contact();
        exampleContact.setAddressId(toDelete.getId());
        List<Contact> contacts = contactRepository.findAll(Example.of(exampleContact));

        contactRepository.deleteAll(contacts);
        addressRepository.delete(toDelete);
    }

}
