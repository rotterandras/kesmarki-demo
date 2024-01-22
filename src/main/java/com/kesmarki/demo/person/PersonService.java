package com.kesmarki.demo.person;

import com.kesmarki.demo.person.dto.CreatePerson;
import com.kesmarki.demo.person.dto.PersonView;
import com.kesmarki.demo.person.dto.SearchPerson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private final PersonRepository personRepository;

    List<PersonView> findAll(SearchPerson searchPerson) {
        Type targetLisType = new TypeToken<List<PersonView>>() {
        }.getType();
        Person examplePerson = modelMapper.map(searchPerson, Person.class);
        List<Person> people = personRepository.findAll(Example.of(examplePerson));
        return modelMapper.map(people, targetLisType);
    }

    PersonView findById(Integer id) {
        throw new UnsupportedOperationException();
    }

    PersonView save(CreatePerson data) {
        throw new UnsupportedOperationException();
    }


}
