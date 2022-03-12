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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
@AutoConfigureMockMvc
// This loads a web ApplicationContext and provides a mock web environment. Embedded servers are not started when using this annotation.
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

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MockMvc restCustomerMockMvc;

    private Customer customer;

    public static Customer createEntity() {
        Customer customer = new Customer(DEFAULT_NAME,
                DEFAULT_GENDER, DEFAULT_EXTERNAL_CUSTOMER_ID,
                DEFAULT_BIRTH_DATE, Instant.now());

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
    void importCsv() throws Exception {
        int databaseSizeBeforeCreate = customerRepository.findAll().size();

        StringBuilder fileContentBuilder = new StringBuilder();
        fileContentBuilder.append("name,gender,birthDate,externalCustomerId\n")
                .append("Joe Soap,MALE,1977-08-30,js@gmail.com\n")
                .append("Josephine Soap,FEMALE,1997-08-30,js2@gmail.com\n");


        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/csv", fileContentBuilder.toString().getBytes());

        // Create the Customer
        restCustomerMockMvc
                .perform(multipart(ENTITY_API_URL + "/import-csv").file(multipartFile))
                .andExpect(status().isCreated());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 2);
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

    @Test
    @Transactional
    void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerRepository.findAll().size();
        // set the field null
        customer.setGender(null);

        // Create the Customer, which fails.

        restCustomerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isBadRequest());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerRepository.findAll().size();
        // set the field null
        customer.setName(null);

        // Create the Customer, which fails.

        restCustomerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isBadRequest());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBirthDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = customerRepository.findAll().size();
        // set the field null
        customer.setBirthDate(null);

        // Create the Customer, which fails.

        restCustomerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isBadRequest());

        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getCustomer() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get the customer
        restCustomerMockMvc
                .perform(get(ENTITY_API_URL_ID, customer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(customer.getId().intValue()))
                .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
                .andExpect(jsonPath("$.externalCustomerId").value(DEFAULT_EXTERNAL_CUSTOMER_ID));
    }

    @Test
    @Transactional
    void getNonExistingCustomer() throws Exception {
        // Get the customer
        restCustomerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCustomer() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        int databaseSizeBeforeUpdate = customerRepository.findAll().size();

        // Update the customer
        Customer existingCustomer = customerRepository.findById(customer.getId()).get();

        Customer customerToUpdate = new Customer();
        customerToUpdate.setId(existingCustomer.getId());
        customerToUpdate.setName(UPDATED_NAME);
        customerToUpdate.setGender(UPDATED_GENDER);
        customerToUpdate.setBirthDate(UPDATED_BIRTH_DATE);
        customerToUpdate.setExternalCustomerId(UPDATED_EXTERNAL_CUSTOMER_ID);
        customerToUpdate.setCreatedAt(existingCustomer.getCreatedAt());
        customerToUpdate.setUpdatedAt(Instant.now());


        restCustomerMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, customerToUpdate.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(customerToUpdate))
                )
                .andExpect(status().isOk());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
        Customer testCustomer = customerList.get(customerList.size() - 1);
        assertThat(testCustomer.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testCustomer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCustomer.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testCustomer.getExternalCustomerId()).isEqualTo(UPDATED_EXTERNAL_CUSTOMER_ID);
    }

    @Test
    @Transactional
    void putNonExistingCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, customer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(customer))
                )
                .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(customer))
                )
                .andExpect(status().isBadRequest());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomer() throws Exception {
        int databaseSizeBeforeUpdate = customerRepository.findAll().size();
        customer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomerMockMvc
                .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(customer)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the Customer in the database
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomer() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        int databaseSizeBeforeDelete = customerRepository.findAll().size();

        // Delete the customer
        restCustomerMockMvc
                .perform(delete(ENTITY_API_URL_ID, customer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Customer> customerList = customerRepository.findAll();
        assertThat(customerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void getAllCustomers() throws Exception {
        // Initialize the database
        customerRepository.saveAndFlush(customer);

        // Get all the customerList
        restCustomerMockMvc
                .perform(get(ENTITY_API_URL + "?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(customer.getId().intValue())))
                .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
                .andExpect(jsonPath("$.[*].externalCustomerId").value(hasItem(DEFAULT_EXTERNAL_CUSTOMER_ID)));
    }
}
