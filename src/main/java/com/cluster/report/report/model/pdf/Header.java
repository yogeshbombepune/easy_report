package com.cluster.report.report.model.pdf;

import lombok.Builder;
import lombok.Data;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.util.Map;

@Data
@Builder
public class Header {
    Map<String, Object> metaKeyVal;
    private PDFont metaKeyValFont;
    private float metaKeyValFontSize;
    private Color metaKeyValColor;
    private String clientName;
    private PDFont clientNameFont;
    private float clientNameFontSize;
    private Color clientNameColor;
    private String reportName;
    private PDFont reportNameFont;
    private float reportNameFontSize;
    private Color reportNameColor;
}
