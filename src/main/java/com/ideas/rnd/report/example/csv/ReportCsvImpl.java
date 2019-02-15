package com.ideas.rnd.report.example.csv;

import com.ideas.rnd.report.algo.CsvReportGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportCsvImpl implements ReportCsv {

	@Override
	public void export(String fileName) throws IOException {
		List<String> columns = columnConfiguration();
		List<List<Object>> dataSet = populateData();
		CsvReportGenerator csvReportGenerator = new CsvReportGenerator("xyz.csv", columns, dataSet);
		csvReportGenerator.generate();

	}

	List<String> columnConfiguration() {
		String[] columns = {"FirstName", "LastName", "Email", "Pin Code", "yes", "column", "column", "column", "column", "Channel"};
		return Arrays.asList(columns);
	}

	@Override
	public List<List<Object>> populateData() {
		Object[] arr = {"FirstName", "LastName", "fakemail@mock,.com", 12345, "yes", "XH4234FSD", 4334.00, "yFone 5 XS", "31/05/2013 07:15 am", "WEB"};
		List<List<Object>> rows = new ArrayList<>();
		for (int row = 0; row < 100; row++) {
			List<Object> modifiableList = new ArrayList(Arrays.asList(arr));
			rows.add(modifiableList);
		}
		return rows;
	}
}
