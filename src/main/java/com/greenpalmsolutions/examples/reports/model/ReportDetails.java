package com.greenpalmsolutions.examples.reports.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDetails {

    private long fileSizeInBytes;
    private byte[] rawPdfFile;
}
