package com.emmett.customermanagement.service;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.domain.Report;
import com.emmett.customermanagement.domain.enumeration.Gender;
import com.emmett.customermanagement.domain.enumeration.ReportType;
import com.emmett.customermanagement.repository.jpa.CustomerRepository;
import com.emmett.customermanagement.repository.jpa.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final CustomerRepository customerRepository;

    private final ReportRepository reportRepository;

    public ReportService(CustomerRepository customerRepository,
                         ReportRepository reportRepository)
    {
        this.customerRepository = customerRepository;
        this.reportRepository = reportRepository;
    }


    public void prepareReport(ReportType reportType) {
        String data = null;
        switch (reportType) {
            case AVG_AGE:
                data = customerRepository.findAvgAge();
                break;

            case AVG_AGE_MALE:
                data = customerRepository.findAvgAgeByGender(Gender.MALE);
                break;

            case AVG_AGE_FEMALE:
                data = customerRepository.findAvgAgeByGender(Gender.FEMALE);
                break;

            default:
                throw new IllegalArgumentException("Unknown report type");


        }

        Report report = new Report(reportType, LocalDateTime.now(), data.toString());
        reportRepository.save(report);
    }



}
