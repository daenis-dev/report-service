package com.greenpalmsolutions.examples.reports.controller;

import com.greenpalmsolutions.examples.reports.behavior.GetReport;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: integration test
@Component
class ReportServiceFactory {

    private final Map<String, GetReport> REPORT_SERVICE_CACHE = new HashMap<>();
    private final List<GetReport> REPORT_SERVICES;

    ReportServiceFactory(List<GetReport> reportServices) {
        this.REPORT_SERVICES = reportServices;
        for (GetReport reportService : this.REPORT_SERVICES) {
            REPORT_SERVICE_CACHE.put(reportService.getClientId(), reportService);
        }
    }

    GetReport getReportServiceForClient(String clientId) {
        GetReport reportService = REPORT_SERVICE_CACHE.get(clientId);
        if (reportService == null) {
            throw new RuntimeException("Received request for unknown report service");
        }
        return reportService;
    }
}
