package com.emmett.customermanagement.service;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.repository.jpa.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public Customer save(Customer customer) {
        log.debug("Request to save Customer : {}", customer);
        return customerRepository.save(customer);
    }


    @Transactional(readOnly = true)
    public Optional<Customer> findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id);
    }

    public Optional<Customer> partialUpdate(Customer customer) {
        log.debug("Request to partially update Customer : {}", customer);

        return customerRepository
                .findById(customer.getId())
                .map(existingCustomer -> {
                    if (customer.getGender() != null) {
                        existingCustomer.setGender(customer.getGender());
                    }
                    if (customer.getName() != null) {
                        existingCustomer.setName(customer.getName());
                    }
                    if (customer.getBirthDate() != null) {
                        existingCustomer.setBirthDate(customer.getBirthDate());
                    }
                    if (customer.getExternalCustomerId() != null) {
                        existingCustomer.setExternalCustomerId(customer.getExternalCustomerId());
                    }

                    return existingCustomer;
                })
                .map(customerRepository::save);
    }

    public void delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        customerRepository.deleteById(id);
    }
}
