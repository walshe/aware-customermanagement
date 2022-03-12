package com.emmett.customermanagement.domain;

import com.emmett.customermanagement.domain.enumeration.ReportType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class Report {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;

    /**
     * we can only have one row in this table for any report type
     */
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", unique = true)
    private ReportType reportType;

    @NotNull
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    @NotNull
    @Column(name = "data", length = 256, nullable = false)
    private String data;

    public Report(){

    }

    public Report(ReportType reportType, LocalDateTime reportDate, String data) {
        this.reportType = reportType;
        this.reportDate = reportDate;
        this.data = data;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
