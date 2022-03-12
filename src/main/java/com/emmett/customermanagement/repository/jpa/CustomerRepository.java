package com.emmett.customermanagement.repository.jpa;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.domain.enumeration.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * this is a Spring Data interface. An Implementation of this interface and all the
 * typical crud methods are generated at compile time.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    @Query(
            value = "select avg(DATEDIFF(YEAR,   birth_date, CURRENT_DATE())) as avg_age from customer",
            nativeQuery = true)
    String findAvgAge();

    @Query(
            value = "select avg(DATEDIFF(YEAR, birth_date, CURRENT_DATE())) as avg_age from customer where gender = :#{#gender?.name()}",
            nativeQuery = true)
    String findAvgAgeByGender(@Param(value = "gender") Gender gender);


}
