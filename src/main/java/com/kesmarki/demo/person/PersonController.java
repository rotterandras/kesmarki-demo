package com.kesmarki.demo.person;

import com.kesmarki.demo.person.dto.PersonView;
import com.kesmarki.demo.person.dto.SearchPerson;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/nyilvantarto/person")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<PersonView> findAll(
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "secondName", required = false) String secondName
    ) {
        SearchPerson searchPerson = new SearchPerson();
        searchPerson.setFirstName(firstName);
        searchPerson.setSecondName(secondName);

        return personService.findAll(searchPerson);
    }


}
