package com.greenpalmsolutions.examples.clientreports;

import com.greenpalmsolutions.examples.reports.behavior.GetReport;
import com.greenpalmsolutions.examples.reports.model.ReportDetails;
import org.springframework.stereotype.Service;

// TODO: Generate PDF and test
@Service
class FinanceReportService implements GetReport {

    @Override
    public String getClientId() {
        return ClientRegistry.FINANCE.getId();
    }

    @Override
    public ReportDetails getReport() {
        return null;
    }
}
