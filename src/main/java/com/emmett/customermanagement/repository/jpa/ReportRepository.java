package com.emmett.customermanagement.repository.jpa;

import com.emmett.customermanagement.domain.Customer;
import com.emmett.customermanagement.domain.Report;
import com.emmett.customermanagement.domain.enumeration.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * this is a Spring Data interface. An Implementation of this interface and all the
 * typical crud methods are generated at compile time.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, ReportType>, JpaSpecificationExecutor<Report> {

    Optional<Report> findByReportType(ReportType reportType);
}
