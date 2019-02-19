package com.ideas.rnd.report.model.excel;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Span {
	Integer firstRow, lastRow, firstCol, lastCol;
}
