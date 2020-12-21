package com.cluster.report.report.model.pdf;

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
