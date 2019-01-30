package com.ideas.rnd.pdf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Graph {
	private List<File> graphs;
}
