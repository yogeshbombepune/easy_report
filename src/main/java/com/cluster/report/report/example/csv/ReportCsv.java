package com.cluster.report.report.example.csv;

import java.io.IOException;
import java.util.List;

public interface ReportCsv {
	void export(String fileName) throws IOException;

	List<List<Object>> populateData();
}
