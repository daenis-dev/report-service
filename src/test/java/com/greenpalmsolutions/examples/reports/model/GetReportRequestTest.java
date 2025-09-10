package com.greenpalmsolutions.examples.reports.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GetReportRequestTest {

    private GetReportRequest getReportRequest;

    @BeforeEach
    void init() {
    }

    @Test
    void setsTheClientId() {
        getReportRequest = new GetReportRequest().withClientId("sample-client");

        assertThat(getReportRequest.getClientId()).isEqualTo("sample-client");
    }

    @Test
    void doesNotSetTheClientIdForNull() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> new GetReportRequest().withClientId(null));

        assertThat(thrown.getMessage()).isEqualTo("Cannot get report without a client ID");
    }

    @Test
    void doesNotSetTheClientIdForEmptyString() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> new GetReportRequest().withClientId(""));

        assertThat(thrown.getMessage()).isEqualTo("Cannot get report without a client ID");
    }
}