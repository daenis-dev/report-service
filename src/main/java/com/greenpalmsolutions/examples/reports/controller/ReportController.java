package com.greenpalmsolutions.examples.reports.controller;

import com.greenpalmsolutions.examples.reports.model.ReportDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: unit test
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportServiceFactory reportServiceFactory;

    @GetMapping("/v1/reports")
    public ResponseEntity<ReportDetails> findReportForClient(@RequestParam("client-id") String clientId) {
        return ResponseEntity.ok(
                reportServiceFactory.getReportServiceForClient(clientId)
                        .getReport());
    }
}
