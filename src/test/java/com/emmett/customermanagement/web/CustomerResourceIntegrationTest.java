package com.emmett.customermanagement.web;

import com.emmett.customermanagement.CustomermanagementApplication;
import com.emmett.customermanagement.TestUtil;
import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.domain.enumeration.Gender;
import com.emmett.customermanagement.repository.jpa.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * From Spring docs: Another useful approach is to not start the server at all but to test only the
 * layer below that, where Spring handles the incoming HTTP request and hands it
 * off to your controller. That way, almost of the full stack is used,
 * and your code will be called in exactly the same way as if it were processing
 * a real HTTP request but without the cost of starting the server.
 * To do that, use Spring’s MockMvc and ask for that to be injected
 * for you by using the @AutoConfigureMockMvc annotation on the test case.
 */

@SpringBootTest(classes = CustomermanagementApplication.class)
@AutoConfigureMockMvc // This loads a web ApplicationContext and provides a mock web environment. Embedded servers are not started when using this annotation.
public class CustomerResourceIntegrationTest {

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTH_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_EXTERNAL_CUSTOMER_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_CUSTOMER_ID = "BBBBBBBBBB";


    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MockMvc restCustomerMockMvc;

    private Customer customer;

    public static Customer createEntity() {
        Customer customer = new Customer(DEFAULT_NAME,
                DEFAULT_GENDER, DEFAULT_EXTERNAL_CUSTOMER_ID,
                DEFAULT_BIRTH_DATE);

        return customer;
    }

    @BeforeEach
    public void initTest() {
        customer = createEntity();
    }

    @Test
    @Transactional
    void createCustomer() throws Exception {
        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        // Create the Customer
        restCustomerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isCreated());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 1);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testCustomer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCustomer.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(testCustomer.getExternalCustomerId()).isEqualTo(DEFAULT_EXTERNAL_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void createCustomerWithExistingId() throws Exception {
        // Create the Customer with an existing ID
        customer.setId(1L);

        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        restCustomerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate);
    }
}
