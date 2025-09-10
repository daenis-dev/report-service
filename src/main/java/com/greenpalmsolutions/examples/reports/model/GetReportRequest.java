package com.greenpalmsolutions.examples.reports.model;

import lombok.Getter;

// TODO: unit test
@Getter
public class GetReportRequest {

    private String clientId;

    public GetReportRequest withClientId(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            throw new RuntimeException("Cannot get report without a client ID");
        }
        this.clientId = clientId;
        return this;
    }
}
