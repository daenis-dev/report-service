package com.greenpalmsolutions.examples.clientreports.informationtechnology;

import com.greenpalmsolutions.examples.clientreports.registry.ClientReportRegistry;
import com.greenpalmsolutions.examples.reports.behavior.GetReport;
import com.greenpalmsolutions.examples.reports.model.ReportDetails;
import com.greenpalmsolutions.examples.utils.PdfReportBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// TODO: integration test
@Service
class InformationTechnologyReportService implements GetReport {

    @Override
    public String getClientId() {
        return ClientReportRegistry.INFORMATION_TECHNOLOGY.getId();
    }

    @Override
    public ReportDetails getReport() {
        final float PDF_MARGIN = 50f;
        final float TITLE_FONT_SIZE = 22f;
        final float ROW_HEIGHT = 22f;
        final float HEADER_FONT_SIZE = 11f;
        final float FONT_SIZE = 12f;
        final float BODY_FONT_SIZE = 10.5f;
        final float LINE_SPACING = 14f;
        final float LINE_SPACING_16 = 16f;
        final float LINE_SPACING_13_5 = 13.5f;

        String[] services = {"Auth", "API", "Database", "Storage", "Messaging"};
        double[] uptimePct        = {99.98, 99.92, 99.95, 99.90, 99.97};
        int[] incidents           = {1, 3, 2, 4, 1};
        double[] mttrMinutes      = {18, 42, 35, 55, 22};
        int[] deploys             = {12, 24, 4, 6, 10};
        double[] changeFailRate   = {0.06, 0.12, 0.05, 0.10, 0.04}; // fraction of deploys that failed

        NumberFormat PF = NumberFormat.getPercentInstance(Locale.US);
        PF.setMinimumFractionDigits(2);
        PF.setMaximumFractionDigits(2);

        List<String[]> rows = new ArrayList<>();
        double uptimeSum = 0;
        int totalIncidents = 0;
        double totalMttrWeighted = 0; // sum(mttr * incidents)
        int totalDeploys = 0;
        double totalFailures = 0;

        for (int i = 0; i < services.length; i++) {
            rows.add(new String[] {
                    services[i],
                    String.format(Locale.US, "%.2f%%", uptimePct[i]),
                    String.valueOf(incidents[i]),
                    String.format(Locale.US, "%.0f", mttrMinutes[i]),
                    String.valueOf(deploys[i]),
                    PF.format(changeFailRate[i])
            });

            uptimeSum += uptimePct[i];
            totalIncidents += incidents[i];
            totalMttrWeighted += mttrMinutes[i] * incidents[i];
            totalDeploys += deploys[i];
            totalFailures += changeFailRate[i] * deploys[i];
        }

        double avgUptime = uptimeSum / services.length;
        double overallMttr = totalIncidents == 0 ? 0 : totalMttrWeighted / totalIncidents;
        double overallCfr = totalDeploys == 0 ? 0 : totalFailures / totalDeploys;

        rows.add(new String[] {
                "OVERALL",
                String.format(Locale.US, "%.2f%%", avgUptime),
                String.valueOf(totalIncidents),
                String.format(Locale.US, "%.0f", overallMttr),
                String.valueOf(totalDeploys),
                PF.format(overallCfr)
        });

        String[] headers = {"Service", "Uptime", "Incidents", "MTTR (min)", "Deploys", "CFR"};

        try (var pdf = PdfReportBuilder.letter().margin(PDF_MARGIN)) {
            pdf.titleCentered("Information Technology Report", TITLE_FONT_SIZE)
                    .labelValue("Generated", LocalDate.now().toString(), HEADER_FONT_SIZE)
                    .labelValue("Scope", "Core platform services", HEADER_FONT_SIZE)
                    .spacer(FONT_SIZE)
                    .table(headers, rows, ROW_HEIGHT, HEADER_FONT_SIZE, BODY_FONT_SIZE)
                    .spacer(LINE_SPACING_16)
                    .paragraph("Summary", FONT_SIZE, LINE_SPACING)
                    .paragraph("• Average uptime across services: " + String.format(Locale.US, "%.2f%%", avgUptime) + ".", BODY_FONT_SIZE, LINE_SPACING_13_5)
                    .paragraph("• " + totalIncidents + " incidents this period; overall MTTR: " + String.format(Locale.US, "%.0f", overallMttr) + " minutes.", BODY_FONT_SIZE, LINE_SPACING_13_5)
                    .paragraph("• " + totalDeploys + " deployments; change failure rate: " + PF.format(overallCfr) + ".", BODY_FONT_SIZE, LINE_SPACING_13_5)
                    .paragraph("• Recommendation: continue canary releases and add runbooks for Storage to reduce incident MTTR.", BODY_FONT_SIZE, LINE_SPACING_13_5);

            byte[] bytes = pdf.toBytes();

            ReportDetails details = new ReportDetails();
            details.setRawPdfFile(bytes);
            details.setFileSizeInBytes(bytes.length);
            return details;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Information Technology Report PDF", e);
        }
    }
}
