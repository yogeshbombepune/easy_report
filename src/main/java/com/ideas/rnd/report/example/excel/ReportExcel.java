package com.ideas.rnd.report.example.excel;

import com.ideas.rnd.report.model.excel.Cell;

import java.io.IOException;
import java.util.List;

public interface ReportExcel {

	void export(String fileName) throws IOException, InterruptedException;

	List<List<Cell>> populateData();
}
