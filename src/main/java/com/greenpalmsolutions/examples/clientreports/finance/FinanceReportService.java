package com.greenpalmsolutions.examples.clientreports.finance;

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
class FinanceReportService implements GetReport {

    @Override
    public String getClientId() {
        return ClientReportRegistry.FINANCE.getId();
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

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        double[] revenue = {120000, 95000, 103000, 110000, 98000, 130000};
        double[] expenses = {85000, 70000, 82000, 83000, 76000, 90000};
        double totalRev = 0, totalExp = 0;
        for (int i = 0; i < months.length; i++) {
            totalRev += revenue[i];
            totalExp += expenses[i];
        }
        double totalProfit = totalRev - totalExp;
        NumberFormat CF = NumberFormat.getCurrencyInstance(Locale.US);

        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            rows.add(new String[] {
                    months[i],
                    CF.format(revenue[i]),
                    CF.format(expenses[i]),
                    CF.format(revenue[i] - expenses[i])
            });
        }
        rows.add(new String[] { "TOTAL", CF.format(totalRev), CF.format(totalExp), CF.format(totalProfit) });

        String[] headers = {"Month", "Revenue", "Expenses", "Profit"};

        try (var pdf = PdfReportBuilder.letter().margin(PDF_MARGIN)) {
            pdf.titleCentered("Finance Report", TITLE_FONT_SIZE)
                    .labelValue("Generated", LocalDate.now().toString(), HEADER_FONT_SIZE)
                    .spacer(FONT_SIZE)
                    .table(headers, rows, ROW_HEIGHT, HEADER_FONT_SIZE, BODY_FONT_SIZE)
                    .spacer(LINE_SPACING_16)
                    .paragraph("Summary", FONT_SIZE, LINE_SPACING)
                    .paragraph("• Revenue outpaced expenses each month; overall profit: " + CF.format(totalProfit) + ".", BODY_FONT_SIZE, LINE_SPACING_13_5)
                    .paragraph("• Strongest month: Jun (" + CF.format(revenue[5] - expenses[5]) + " profit).", BODY_FONT_SIZE, LINE_SPACING_13_5)
                    .paragraph("• Recommendation: reinvest 10% of profits into growth initiatives next quarter.", BODY_FONT_SIZE, LINE_SPACING_13_5);

            byte[] bytes = pdf.toBytes();

            ReportDetails details = new ReportDetails();
            details.setRawPdfFile(bytes);
            details.setFileSizeInBytes(bytes.length);
            return details;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Finance Report PDF", e);
        }
    }
}

