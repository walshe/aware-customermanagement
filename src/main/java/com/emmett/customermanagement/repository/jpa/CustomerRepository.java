package com.emmett.customermanagement.repository.jpa;

import com.emmett.customermanagement.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * this is a Spring Data interface. An Implementation of this interface and all the
 * typical crud methods are generated at compile time.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {}
