package com.kesmarki.demo.contact;

import com.kesmarki.demo.contact.dto.ContactView;
import com.kesmarki.demo.contact.dto.CreateContact;
import com.kesmarki.demo.contact.dto.UpdateContact;
import com.kesmarki.demo.person.PersonService;
import com.kesmarki.demo.person.dto.CreatePerson;
import com.kesmarki.demo.person.dto.PersonView;
import com.kesmarki.demo.person.dto.SearchPerson;
import com.kesmarki.demo.person.dto.UpdatePerson;
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
@RequestMapping("/api/nyilvantarto/contact")
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContactView> findAll() {
        log.info("GET request /contact invoke findAll()");
        return contactService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContactView findById(@PathVariable("id") @PositiveOrZero int id) {
        log.info("GET request /contact/id/{} invoke findById({})", id, id);
        return contactService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactView save(@Valid @RequestBody CreateContact command) {
        log.info("POST request /contact invoke save({})", command);
        return contactService.save(command);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContactView update(@PathVariable @PositiveOrZero int id, @Valid @RequestBody UpdateContact command) {
        log.info("PUT request /contact/{} invoke update({}, {})", id, id, command);
        return contactService.update(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable("id") @PositiveOrZero int id) {
        log.info("DELETE request /contact/{} invoke deleteById({})", id, id);
        contactService.deleteById(id);
    }

}
