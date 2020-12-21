package com.cluster.report.report.model.excel;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Font {
	private Short color;
	private String fontName;
	private Short fontHeight;
	private Boolean italic;
	private Boolean bold;
	private Boolean strikeout;
	private Byte underline;
}
