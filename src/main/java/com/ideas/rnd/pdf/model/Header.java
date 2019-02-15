package com.ideas.rnd.pdf.model;

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
    private String propertyName;
    private PDFont propertyNameFont;
    private float propertyNameFontSize;
    private Color propertyNameColor;
    private String reportName;
    private PDFont reportNameFont;
    private float reportNameFontSize;
    private Color reportNameColor;
}
