package com.ideas.rnd.pdf.model;

import lombok.Builder;
import lombok.Data;

import java.awt.*;
import java.io.File;

@Data
@Builder
public class Footer {
	private String pageNumberPhrase;
	private File logoImage;
	private Color lineColor;
	private float lineWidth;
}
