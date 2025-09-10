package it.clientreports.finance;

import com.greenpalmsolutions.examples.reports.controller.ReportController;
import com.greenpalmsolutions.examples.reports.model.ReportDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GetFinanceReportIT {

    @Autowired
    private ReportController reportController;

    @Test
    void getTheFinanceReport() {
        ResponseEntity<ReportDetails> theResponse = reportController.findReportForClient("finance");

        ReportDetails theReport = theResponse.getBody();
        byte[] theRawFile = theReport.getRawPdfFile();
        long theFileSize = theReport.getFileSizeInBytes();

        assertThat(theRawFile).isNotEmpty();
        assertThat(theFileSize).isEqualTo(theRawFile.length);
    }
}
