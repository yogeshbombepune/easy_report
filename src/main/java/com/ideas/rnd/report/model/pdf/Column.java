package com.ideas.rnd.report.model.pdf;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Column {
	private String name;
	private float width;
	private Alignment headerAlignment;
	private Alignment contentAlignment;
}
