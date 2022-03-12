package com.emmett.customermanagement.web;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.repository.jpa.CustomerRepository;
import com.emmett.customermanagement.service.CustomerService;
import com.emmett.customermanagement.web.errors.BadRequestAlertException;
import com.emmett.customermanagement.web.util.HeaderUtil;
import com.emmett.customermanagement.web.util.PaginationUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CustomerResource {

    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String ENTITY_NAME = "customer";

    private final CustomerService customerService;

    private final CustomerRepository customerRepository;

    public CustomerResource(
            CustomerService customerService, CustomerRepository customerRepository
    ) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
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

    /**
     * Full update of a customer
     *
     * @param id
     * @param customer
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Customer customer
    ) throws URISyntaxException {
        log.debug("REST request to update Customer : {}, {}", id, customer);
        if (customer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Customer result = customerService.save(customer);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customer.getId().toString()))
                .body(result);
    }

    /**
     * Partial update of a customer
     *
     * @param id
     * @param customer
     * @return
     * @throws URISyntaxException
     */
    @PatchMapping(value = "/customers/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<Customer> partialUpdateCustomer(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody Customer customer
    ) throws URISyntaxException {
        log.debug("REST request to partial update Customer partially : {}, {}", id, customer);
        if (customer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Customer> result = customerService.partialUpdate(customer);

        return result.map(response -> ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customer.getId().toString())).body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.debug("REST request to delete Customer : {}", id);
        customerService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
    }

    /**
     * Rudimentary count that does not involve any criteria
     *
     * @return
     */
    @GetMapping("/customers/count")
    public ResponseEntity<Long> countCustomers() {
        log.debug("REST request to count Customers");
        return ResponseEntity.ok().body(customerRepository.count());
    }

    /**
     * Rudimentary "getAll" - with no criteria for now. Just pages a result set that's currently sorted by name ascending
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(
            @RequestParam(required = true, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "20") int size
    ) {
        log.debug("REST request to get page of Customers page {}, size {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Customer> returnedPage = customerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(returnedPage, "/api/users");
        return ResponseEntity.ok().headers(headers).body(returnedPage.getContent());
    }
}