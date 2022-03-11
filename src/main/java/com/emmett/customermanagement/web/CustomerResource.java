package com.emmett.customermanagement.web;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.service.CustomerService;
import com.emmett.customermanagement.web.errors.BadRequestAlertException;
import com.emmett.customermanagement.web.util.HeaderUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CustomerResource {

    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String ENTITY_NAME = "customer";

    private final CustomerService customerService;

    public CustomerResource(
            CustomerService customerService
    ) {
        this.customerService = customerService;
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) throws URISyntaxException {
        log.debug("REST request to save Customer : {}", customer);
        if (customer.getId() != null) {
            throw new BadRequestAlertException("A new customer cannot already have an ID", ENTITY_NAME, "idexists");
        }

        try {
            Customer result = customerService.save(customer);
            HttpHeaders headers = new HttpHeaders();
            return ResponseEntity
                    .created(new URI("/api/customers/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                    .body(result);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                if (((ConstraintViolationException) (ex.getCause())).getSQLState().equals("23505")) {
                    throw new BadRequestAlertException("A customer with this externalCustomerId exists already", ENTITY_NAME, "externalIdexists");
                }
            }
            throw ex;

        }
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        log.debug("REST request to get Customer : {}", id);
        Optional<Customer> customer = customerService.findOne(id);
        return customer.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
