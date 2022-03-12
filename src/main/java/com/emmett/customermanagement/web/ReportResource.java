package com.emmett.customermanagement.web;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.domain.Report;
import com.emmett.customermanagement.domain.enumeration.ReportType;
import com.emmett.customermanagement.repository.jpa.CustomerRepository;
import com.emmett.customermanagement.repository.jpa.ReportRepository;
import com.emmett.customermanagement.service.CustomerService;
import com.emmett.customermanagement.service.ReportService;
import com.emmett.customermanagement.web.errors.BadRequestAlertException;
import com.emmett.customermanagement.web.util.HeaderUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReportResource {

    private final Logger log = LoggerFactory.getLogger(ReportResource.class);

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String ENTITY_NAME = "report";

    private final ReportService reportService;

    private final ReportRepository reportRepository;

    public ReportResource(
            ReportService reportService,
            ReportRepository reportRepository
    ) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }


    @GetMapping("/reports/{reportType}")
    public ResponseEntity<Report> getReport(@PathVariable(required = true) ReportType reportType) {
        log.debug("REST request to get Report : {}", reportType);

        //Optional<Report> report = reportRepository.findOne(reportType);
        Optional<Report> report = reportRepository.findByReportType(reportType);
        return report.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    /**
     * Prepares a report - typically this would be run once a day at night. It calculates the report
     * and 'caches' it to the report table where it can be read quickly from our GET api
     * @param reportType
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/reports/{reportType}")
    public ResponseEntity<Void> updateReport(
            @PathVariable(value = "reportType", required = true) final ReportType reportType
    ) throws URISyntaxException {
        log.debug("REST request to update ReportType : {}", reportType);


        reportService.prepareReport(reportType);

        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, reportType.toString()))
                .build();


    }


}
