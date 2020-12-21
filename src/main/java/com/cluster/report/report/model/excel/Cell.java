package com.cluster.report.report.model.excel;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Cell {
	private Object value;
	private Font font;
	private CellStyle cellStyle;
	private Span span;
}
