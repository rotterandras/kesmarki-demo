package com.kesmarki.demo.address;

import com.kesmarki.demo.address.dto.AddressView;
import com.kesmarki.demo.address.dto.CreateAddress;
import com.kesmarki.demo.address.dto.SearchAddress;
import com.kesmarki.demo.address.dto.UpdateAddress;
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
@RequestMapping("/api/nyilvantarto/address")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AddressView> findAll(
            @RequestParam(name = "type", required = false) AddressType type,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "street", required = false) String street
    ) {
        SearchAddress searchAddress = new SearchAddress();
        searchAddress.setType(type);
        searchAddress.setCity(city);
        searchAddress.setStreet(street);

        log.info("GET request /address invoke findAll({})", searchAddress);
        return addressService.findAll(searchAddress);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AddressView findById(@PathVariable("id") @PositiveOrZero int id) {
        log.info("GET request /address/id/{} invoke findById({})", id, id);
        return addressService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressView save(@Valid @RequestBody CreateAddress command) {
        log.info("POST request /address invoke save({})", command);
        return addressService.save(command);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AddressView update(@PathVariable @PositiveOrZero int id, @Valid @RequestBody UpdateAddress command) {
        log.info("PUT request /address/{} invoke update({}, {})", id, id, command);
        return addressService.update(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable("id") @PositiveOrZero int id) {
        log.info("DELETE request /address/{} invoke deleteById({})", id, id);
        addressService.deleteById(id);
    }

}
