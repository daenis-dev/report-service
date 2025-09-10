package com.greenpalmsolutions.examples.clientreports.marketing;

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

@Service
class MarketingReportService implements GetReport {

    @Override
    public String getClientId() {
        return ClientReportRegistry.MARKETING.getId();
    }

    @Override
    public ReportDetails getReport() {
        final float PDF_MARGIN = 50f;
        final float TITLE_FONT_SIZE = 22f;
        final float ROW_HEIGHT = 20f;
        final float HEADER_FONT_SIZE = 10f;
        final float FONT_SIZE = 12f;
        final float BODY_FONT_SIZE = 9.5f;
        final float LINE_SPACING = 14f;
        final float LINE_SPACING_16 = 16f;
        final float LINE_SPACING_13_5 = 13.5f;

        String[] channels = {"Search", "Social", "Display", "Email", "Referral"};
        double[] impressions = {540_000, 320_000, 180_000, 90_000, 60_000};
        double[] clicks      = {27_000, 12_800, 3_600, 2_700, 1_800};
        double[] spend       = {42_000, 25_000,  9_000, 3_000, 1_500};
        double[] conversions = {   850,    410,     75,   120,    45};
        double avgRevenuePerConv = 120.0; // mock value for ROAS

        NumberFormat CF = NumberFormat.getCurrencyInstance(Locale.US);
        NumberFormat PF = NumberFormat.getPercentInstance(Locale.US);
        PF.setMinimumFractionDigits(1);
        PF.setMaximumFractionDigits(1);

        double totalImpr = 0, totalClicks = 0, totalSpend = 0, totalConv = 0, totalRevenue = 0;
        int bestRoasIdx = 0;
        double bestRoas = -1;

        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < channels.length; i++) {
            double ctr = clicks[i] / impressions[i];
            double revenue = conversions[i] * avgRevenuePerConv;
            double roas = spend[i] == 0 ? 0 : revenue / spend[i];
            double cpa = conversions[i] == 0 ? 0 : spend[i] / conversions[i];

            if (roas > bestRoas) { bestRoas = roas; bestRoasIdx = i; }

            rows.add(new String[] {
                    channels[i],
                    String.valueOf((long) impressions[i]),
                    String.valueOf((long) clicks[i]),
                    PF.format(ctr),
                    String.valueOf((long) conversions[i]),
                    CF.format(spend[i]),
                    CF.format(cpa),
                    String.format(Locale.US, "%.1fx", roas)
            });

            totalImpr += impressions[i];
            totalClicks += clicks[i];
            totalSpend += spend[i];
            totalConv += conversions[i];
            totalRevenue += revenue;
        }

        double totalCtr = totalClicks / totalImpr;
        double totalCpa = totalConv == 0 ? 0 : totalSpend / totalConv;
        double totalRoas = totalSpend == 0 ? 0 : totalRevenue / totalSpend;

        rows.add(new String[] {
                "TOTAL",
                String.valueOf((long) totalImpr),
                String.valueOf((long) totalClicks),
                PF.format(totalCtr),
                String.valueOf((long) totalConv),
                CF.format(totalSpend),
                CF.format(totalCpa),
                String.format(Locale.US, "%.1fx", totalRoas)
        });

        String[] headers = {"Channel", "Impr.", "Clicks", "CTR", "Conv.", "Spend", "CPA", "ROAS"};

        try (var pdf = PdfReportBuilder.letter().margin(PDF_MARGIN)) {
            pdf.titleCentered("Marketing Report", TITLE_FONT_SIZE)
                    .labelValue("Generated", LocalDate.now().toString(), HEADER_FONT_SIZE)
                    .labelValue("Period", "Last 30 days", HEADER_FONT_SIZE)
                    .spacer(FONT_SIZE)
                    .table(headers, rows, ROW_HEIGHT, HEADER_FONT_SIZE, BODY_FONT_SIZE)
                    .spacer(LINE_SPACING_16)
                    .paragraph("Summary", FONT_SIZE, LINE_SPACING)
                    .paragraph("• Total spend: " + CF.format(totalSpend) + "; Total conversions: " + (long) totalConv + ".", BODY_FONT_SIZE, LINE_SPACING_13_5)
                    .paragraph("• Overall CTR: " + PF.format(totalCtr) + "; CPA: " + CF.format(totalCpa) + "; ROAS: " + String.format(Locale.US, "%.1fx", totalRoas) + ".", BODY_FONT_SIZE, LINE_SPACING_13_5)
                    .paragraph("• Top channel by ROAS: " + channels[bestRoasIdx] + " (" + String.format(Locale.US, "%.1fx", bestRoas) + ").", BODY_FONT_SIZE, LINE_SPACING_13_5);

            byte[] bytes = pdf.toBytes();

            ReportDetails details = new ReportDetails();
            details.setRawPdfFile(bytes);
            details.setFileSizeInBytes(bytes.length);
            return details;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Marketing Report PDF", e);
        }
    }
}
