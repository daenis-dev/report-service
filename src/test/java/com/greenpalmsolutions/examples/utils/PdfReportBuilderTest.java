package com.greenpalmsolutions.examples.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfReportBuilderTest {

    @Test
    void buildsPdfWithTitleLabelParagraphAndTable() throws Exception {
        String[] headers = {"Month","Revenue","Expenses","Profit"};
        List<String[]> rows = List.of(
                new String[]{"Jan","100","50","50"},
                new String[]{"Feb","200","120","80"},
                new String[]{"TOTAL","300","170","130"}
        );

        byte[] bytes;
        try (var pdf = PdfReportBuilder.letter().margin(50f)) {
            pdf.titleCentered("Test Report", 18f)
                    .labelValue("Generated", "2025-09-10", 11f)
                    .paragraph("This is a long paragraph that should wrap across multiple lines to verify simple word wrapping works correctly.",
                            10.5f, 13.5f)
                    .spacer(12f)
                    .table(headers, rows, 20f, 10f, 10f);
            bytes = pdf.toBytes();
        }

        assertNotNull(bytes);
        assertTrue(bytes.length > 500);

        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(bytes))) {
            assertEquals(1, doc.getNumberOfPages());
            String text = new PDFTextStripper().getText(doc);
            assertTrue(text.contains("Test Report"));
            assertTrue(text.contains("Generated"));
            assertTrue(text.contains("Month"));
            assertTrue(text.contains("TOTAL"));
            assertTrue(text.contains("This is a long paragraph"));
        }
    }

    @Test
    void spacerTriggersNewPage() throws Exception {
        byte[] bytes;
        try (var pdf = PdfReportBuilder.letter()) {
            pdf.titleCentered("Page 1", 16f)
                    .spacer(5000f) // exceed page height -> triggers new page
                    .titleCentered("Page 2", 16f);
            bytes = pdf.toBytes();
        }

        try (PDDocument doc = PDDocument.load(bytes)) {
            assertTrue(doc.getNumberOfPages() >= 2);
            String text = new PDFTextStripper().getText(doc);
            assertTrue(text.contains("Page 1"));
            assertTrue(text.contains("Page 2"));
        }
    }

    @Test
    void usesCustomPageSize() throws Exception {
        byte[] bytes;
        try (var pdf = PdfReportBuilder.of(PDRectangle.A4)) {
            pdf.titleCentered("A4 Doc", 16f);
            bytes = pdf.toBytes();
        }

        try (PDDocument doc = PDDocument.load(bytes)) {
            var mb = doc.getPage(0).getMediaBox();
            assertEquals(PDRectangle.A4.getWidth(), mb.getWidth(), 0.1);
            assertEquals(PDRectangle.A4.getHeight(), mb.getHeight(), 0.1);
        }
    }

    @Test
    void paragraphWrapsAcrossLines() throws Exception {
        String longText =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";

        byte[] bytes;
        try (var pdf = PdfReportBuilder.letter().margin(100f)) { // narrower width -> more wrapping
            pdf.titleCentered("Wrap Test", 16f)
                    .paragraph(longText, 10.5f, 12.5f);
            bytes = pdf.toBytes();
        }

        try (PDDocument doc = PDDocument.load(bytes)) {
            String text = new PDFTextStripper().getText(doc);
            assertTrue(text.contains("Wrap Test"));
            assertTrue(text.contains("Lorem ipsum dolor sit amet"));
            assertTrue(text.contains("commodo consequat"));
        }
    }

    @Test
    void tableRendersAllProvidedCells() throws Exception {
        String[] headers = {"H1","H2","H3"};
        List<String[]> rows = List.of(
                new String[]{"r1c1","r1c2","r1c3"},
                new String[]{"r2c1","r2c2","r2c3"}
        );

        byte[] bytes;
        try (var pdf = PdfReportBuilder.letter()) {
            pdf.titleCentered("Table Test", 16f)
                    .table(headers, rows, 18f, 10f, 10f);
            bytes = pdf.toBytes();
        }

        try (PDDocument doc = PDDocument.load(bytes)) {
            String text = new PDFTextStripper().getText(doc);
            assertTrue(text.contains("H1"));
            assertTrue(text.contains("H2"));
            assertTrue(text.contains("H3"));
            assertTrue(text.contains("r1c1"));
            assertTrue(text.contains("r2c3"));
        }
    }
}
