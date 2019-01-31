package com.ideas.rnd.pdf.model;

import lombok.Builder;
import lombok.Data;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.File;

@Data
@Builder
public class Footer {
	private String pageNumberPhrase;
	private Color pageNumberPhraseColor;
	private PDFont pageNumberPhraseFont;
	private float pageNumberPhraseSize;
	private File logoImage;
	private Color lineColor;
	private float lineWidth;
}
