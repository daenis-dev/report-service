package com.greenpalmsolutions.examples.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

// TODO: unit test
public class PdfReportBuilder implements AutoCloseable {

    private final PDDocument doc = new PDDocument();
    private PDPageContentStream cs;
    private PDPage page;
    private PDRectangle pageSize;

    private float margin = 50f;
    private float cursorY;

    private PDFont font = PDType1Font.HELVETICA;
    private PDFont fontBold = PDType1Font.HELVETICA_BOLD;

    public static PdfReportBuilder letter() {
        return new PdfReportBuilder(PDRectangle.LETTER);
    }

    public static PdfReportBuilder of(PDRectangle size) {
        return new PdfReportBuilder(size);
    }

    private PdfReportBuilder(PDRectangle size) {
        this.pageSize = size;
        newPage();
    }

    public PdfReportBuilder margin(float points) {
        this.margin = points;
        this.cursorY = Math.min(cursorY, pageSize.getHeight() - margin);
        return this;
    }

    public PdfReportBuilder titleCentered(String title, float fontSize) throws IOException {
        float titleWidth = fontBold.getStringWidth(title) / 1000f * fontSize;
        float x = (pageSize.getWidth() - titleWidth) / 2f;
        ensureSpace(fontSize + 10);
        cs.beginText();
        cs.setFont(fontBold, fontSize);
        cs.newLineAtOffset(x, cursorY);
        cs.showText(title);
        cs.endText();
        cursorY -= fontSize + 10;
        return this;
    }

    public PdfReportBuilder labelValue(String label, String value, float fontSize) throws IOException {
        ensureSpace(fontSize * 1.4f);
        cs.beginText();
        cs.setFont(fontBold, fontSize);
        cs.newLineAtOffset(margin, cursorY);
        cs.showText(label + ": ");
        cs.endText();

        float labelWidth = fontBold.getStringWidth(label + ": ") / 1000f * fontSize;
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(margin + labelWidth, cursorY);
        cs.showText(value);
        cs.endText();

        cursorY -= fontSize * 1.4f;
        return this;
    }

    public PdfReportBuilder paragraph(String text, float fontSize, float lineSpacing) throws IOException {
        float availableWidth = pageSize.getWidth() - 2 * margin;
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();

        for (String w : words) {
            String trial = line.length() == 0 ? w : line + " " + w;
            float lineWidth = font.getStringWidth(trial) / 1000f * fontSize;

            if (lineWidth > availableWidth) {
                writeLine(line.toString(), fontSize);
                cursorY -= lineSpacing;
                line = new StringBuilder(w);
            } else {
                line = new StringBuilder(trial);
            }
        }
        if (line.length() > 0) {
            writeLine(line.toString(), fontSize);
            cursorY -= lineSpacing;
        }
        return this;
    }

    public PdfReportBuilder table(String[] headers, List<String[]> rows, float rowHeight, float headerFont, float bodyFont) throws IOException {
        int cols = headers.length;
        float tableWidth = pageSize.getWidth() - 2 * margin;
        float colWidth = tableWidth / cols;
        float totalRows = rows.size() + 1;
        float tableHeight = totalRows * rowHeight;

        ensureSpace(tableHeight + 6);

        float top = cursorY;

        cs.setLineWidth(0.6f);
        for (int i = 0; i <= totalRows; i++) {
            float y = top - i * rowHeight;
            cs.moveTo(margin, y);
            cs.lineTo(margin + tableWidth, y);
            cs.stroke();
        }
        for (int i = 0; i <= cols; i++) {
            float x = margin + i * colWidth;
            cs.moveTo(x, top);
            cs.lineTo(x, top - tableHeight);
            cs.stroke();
        }

        float cellPad = 5f;

        for (int c = 0; c < cols; c++) {
            cs.beginText();
            cs.setFont(fontBold, headerFont);
            cs.newLineAtOffset(margin + c * colWidth + cellPad, top - (rowHeight * 0.65f));
            cs.showText(headers[c]);
            cs.endText();
        }

        for (int r = 0; r < rows.size(); r++) {
            String[] row = rows.get(r);
            for (int c = 0; c < cols; c++) {
                String val = c < row.length ? row[c] : "";
                cs.beginText();
                cs.setFont(font, bodyFont);
                cs.newLineAtOffset(margin + c * colWidth + cellPad, top - ((r + 1) * rowHeight) - (rowHeight * 0.65f));
                cs.showText(val);
                cs.endText();
            }
        }

        cursorY = top - tableHeight - 6;
        return this;
    }

    public PdfReportBuilder spacer(float points) {
        cursorY -= points;
        if (cursorY < margin) {
            newPage();
        }
        return this;
    }

    public byte[] toBytes() throws IOException {
        closeContentStreamIfOpen();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            doc.save(baos);
            return baos.toByteArray();
        } finally {
            doc.close();
        }
    }

    private void writeLine(String text, float fontSize) throws IOException {
        ensureSpace(fontSize * 1.4f);
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(margin, cursorY);
        cs.showText(text);
        cs.endText();
    }

    private void ensureSpace(float needed) {
        if (cursorY - needed < margin) {
            newPage();
        }
    }

    private void newPage() {
        try {
            closeContentStreamIfOpen();
            page = new PDPage(pageSize);
            doc.addPage(page);
            cs = new PDPageContentStream(doc, page);
            cursorY = pageSize.getHeight() - margin;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open PDF content stream", e);
        }
    }

    private void closeContentStreamIfOpen() throws IOException {
        if (cs != null) {
            cs.close();
            cs = null;
        }
    }

    @Override
    public void close() throws IOException {
        closeContentStreamIfOpen();
        doc.close();
    }
}
