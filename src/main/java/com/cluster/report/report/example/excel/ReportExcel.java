package com.cluster.report.report.example.excel;

import java.io.IOException;
import java.util.List;

import com.cluster.report.report.model.excel.Cell;

public interface ReportExcel {

	void export(String fileName) throws IOException, InterruptedException;

	List<List<Cell>> populateData();
}
