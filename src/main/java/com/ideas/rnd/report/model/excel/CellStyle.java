package com.ideas.rnd.report.model.excel;

import com.ideas.rnd.report.model.pdf.Alignment;
import lombok.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class CellStyle {
	private String format;
	private Boolean wrapText;
	private Alignment alignment;
	private BorderStyle borderBottom;
	private BorderStyle borderLeft;
	private BorderStyle borderRight;
	private BorderStyle borderTop;
	private Short topBorderColor;
	private Short leftBorderColor;
	private Short rightBorderColor;
	private Short bottomBorderColor;
	private FillPatternType fillPatternType;
	private Short fillForegroundColor;
}
