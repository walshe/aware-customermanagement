package com.emmett.customermanagement.service;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.domain.enumeration.Gender;
import com.emmett.customermanagement.repository.jpa.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

public class CustomerServiceIntegrationTest {

    @InjectMocks // this will inject the customerRepositoryMock into the CustomerService we want to test
	CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    @BeforeEach
    public void init() throws Exception{

        //inits mocks
        MockitoAnnotations.openMocks(this).close();

    }

    @Test
    void save() throws Exception {
        Customer customer = new Customer("unit test", Gender.FEMALE,
                "externalId",LocalDate.ofEpochDay(0L));

        //set expectation
        when(customerRepository.save(customer)).thenReturn(customer);

        //hit the code to test
        Customer returned = customerService.save(customer);

        //verify expecations
        assertThat(returned).isEqualTo(customer);
		verify(customerRepository, timeout(1)).save(customer);
    }
}
