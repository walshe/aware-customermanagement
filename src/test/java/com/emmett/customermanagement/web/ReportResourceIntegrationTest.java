package com.emmett.customermanagement.web;

import com.emmett.customermanagement.CustomermanagementApplication;
import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.domain.Report;
import com.emmett.customermanagement.domain.enumeration.Gender;
import com.emmett.customermanagement.domain.enumeration.ReportType;
import com.emmett.customermanagement.repository.jpa.CustomerRepository;
import com.emmett.customermanagement.repository.jpa.ReportRepository;
import com.emmett.customermanagement.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
@AutoConfigureMockMvc
// This loads a web ApplicationContext and provides a mock web environment. Embedded servers are not started when using this annotation.
public class ReportResourceIntegrationTest {

    private static final String ENTITY_API_URL = "/api/reports";


    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MockMvc restReportMockMvc;

    /**
     * lets create some males born on 1970-01-01 and some females born 10 years later
     */
    private List<Customer> customers = Arrays.asList(
                new Customer("male_1", Gender.MALE, null, LocalDate.ofEpochDay(0L), Instant.now()),
                new Customer("male_2", Gender.MALE, null, LocalDate.ofEpochDay(0L), Instant.now()),
                new Customer("male_3", Gender.MALE, null, LocalDate.ofEpochDay(0L), Instant.now()),
                new Customer("male_4", Gender.MALE, null, LocalDate.ofEpochDay(0L), Instant.now()),
                new Customer("male_5", Gender.MALE, null, LocalDate.ofEpochDay(0L), Instant.now()),
                new Customer("female_1", Gender.FEMALE, null, LocalDate.ofEpochDay(366 * 10), Instant.now()),
                new Customer("female_2", Gender.FEMALE, null, LocalDate.ofEpochDay(366 * 10), Instant.now()),
                new Customer("female_3", Gender.FEMALE, null, LocalDate.ofEpochDay(366 * 10), Instant.now()),
                new Customer("female_4", Gender.FEMALE, null, LocalDate.ofEpochDay(366 * 10), Instant.now()),
                new Customer("female_5", Gender.FEMALE, null, LocalDate.ofEpochDay(366 * 10), Instant.now())
        );

    private int expectedAverageAge = (int)this.customers.stream().mapToInt(c-> Period.between(c.getBirthDate(), LocalDate.now()).getYears()  ).average().getAsDouble();
    private int expectedAverageMaleAge = (int)this.customers.stream().filter(c -> c.getGender() == Gender.MALE).mapToInt(c-> Period.between(c.getBirthDate(), LocalDate.now()).getYears()  ).average().getAsDouble();
    private int expectedAverageFemaleAge = (int)this.customers.stream().filter(c -> c.getGender() == Gender.FEMALE).mapToInt(c-> Period.between(c.getBirthDate(), LocalDate.now()).getYears()  ).average().getAsDouble();
    

    @BeforeEach
    public void initTest() {
        customerRepository.saveAll(this.customers);
    }

    @Test
    @Transactional
    void prepareAverageAgeReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Prepare the report
        restReportMockMvc
                .perform(put(ENTITY_API_URL + "/AVG_AGE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the report in the db
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void getAverageAgeReport() throws Exception {

        //prepare report in the db
        reportService.prepareReport(ReportType.AVG_AGE);

        restReportMockMvc
                .perform(get(ENTITY_API_URL + "/AVG_AGE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Validate the report in the db
        Optional<Report> report = reportRepository.findByReportType(ReportType.AVG_AGE);
        assertThat(report.get().getData()).isEqualTo( String.valueOf(this.expectedAverageAge));
    }

    @Test
    @Transactional
    void prepareAverageAgeMaleReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Prepare the report
        restReportMockMvc
                .perform(put(ENTITY_API_URL + "/AVG_AGE_MALE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the report in the db
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void getAverageAgeMaleReport() throws Exception {

        //prepare report in the db
        reportService.prepareReport(ReportType.AVG_AGE_MALE);

        restReportMockMvc
                .perform(get(ENTITY_API_URL + "/AVG_AGE_MALE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Validate the report in the db
        Optional<Report> report = reportRepository.findByReportType(ReportType.AVG_AGE_MALE);
        assertThat(report.get().getData()).isEqualTo( String.valueOf(this.expectedAverageMaleAge));
    }


    @Test
    @Transactional
    void prepareAverageAgeFemaleReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Prepare the report
        restReportMockMvc
                .perform(put(ENTITY_API_URL + "/AVG_AGE_FEMALE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the report in the db
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void getAverageAgeFemaleReport() throws Exception {

        //prepare report in the db
        reportService.prepareReport(ReportType.AVG_AGE_FEMALE);

        restReportMockMvc
                .perform(get(ENTITY_API_URL + "/AVG_AGE_FEMALE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Validate the report in the db
        Optional<Report> report = reportRepository.findByReportType(ReportType.AVG_AGE_FEMALE);
        assertThat(report.get().getData()).isEqualTo( String.valueOf(this.expectedAverageFemaleAge));
    }


}
