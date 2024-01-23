package com.kesmarki.demo.person;

import com.kesmarki.demo.person.dto.*;
import com.kesmarki.demo.validation.Name;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
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
    public List<PersonView> findAll(
            @RequestParam(name = "firstName", required = false) @Name(isOptional = true) String firstName,
            @RequestParam(name = "secondName", required = false) @Name(isOptional = true) String secondName
    ) {
        SearchPerson searchPerson = new SearchPerson();
        searchPerson.setFirstName(firstName);
        searchPerson.setSecondName(secondName);

        log.info("GET request /person invoke findAll({})", searchPerson);
        return personService.findAll(searchPerson);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonInfo findById(@PathVariable("id") @PositiveOrZero int id) {
        log.info("GET request /person/id/{} invoke findById({})", id, id);
        return personService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonView save(@Valid @RequestBody CreatePerson command) {
        log.info("POST request /person invoke save({})", command);
        return personService.save(command);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonView update(@PathVariable @PositiveOrZero int id, @Valid @RequestBody UpdatePerson command) {
        log.info("PUT request /person/{} invoke update({}, {})", id, id, command);
        return personService.update(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable("id") @PositiveOrZero int id) {
        log.info("DELETE request /person/{} invoke deleteById({})", id, id);
        personService.deleteById(id);
    }

}
