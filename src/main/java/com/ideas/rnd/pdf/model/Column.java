package com.ideas.rnd.pdf.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Column {
	private String name;
	private float width;
}
