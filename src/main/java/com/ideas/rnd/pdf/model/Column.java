package com.ideas.rnd.pdf.model;

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
	private Alignment alignment;
}
