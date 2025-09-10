package com.greenpalmsolutions.examples.reports.controller;

import com.greenpalmsolutions.examples.reports.behavior.GetReport;
import com.greenpalmsolutions.examples.reports.model.ReportDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportServiceFactory reportServiceFactory;

    @Mock
    private GetReport getReport;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReportController(reportServiceFactory))
                .build();
    }

    @Test
    void getsTheReportForTheClient() throws Exception {
        final String CLIENT_ID = "sample-client";
        byte[] rawFile = new byte[]{1, 0, 0, 1};
        ReportDetails reportDetails = new ReportDetails();
        reportDetails.setFileSizeInBytes(rawFile.length);
        reportDetails.setRawPdfFile(rawFile);

        when(reportServiceFactory.getReportServiceForClient(CLIENT_ID)).thenReturn(getReport);
        when(getReport.getReport()).thenReturn(reportDetails);

        mockMvc.perform(get("/v1/reports")
                        .param("client-id", CLIENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileSizeInBytes", is(rawFile.length)));
    }
}