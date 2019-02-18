package com.ideas.rnd.report.example.csv;

import com.ideas.rnd.report.algo.CsvReportGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ReportCsvImpl implements ReportCsv {

	@Override
	public void export(String fileName) throws IOException {
		Map dataMap = new LinkedHashMap();
		List<String> columns = columnConfiguration();
		List<List<Object>> dataSet = populateData();
		CsvReportGenerator csvReportGenerator = new CsvReportGenerator(fileName, columns, dataSet);
		csvReportGenerator.generate();
		if (dataMap.containsKey("outputStream")) {
			OutputStream out = (OutputStream) dataMap.get("outputStream");
			Files.copy(Paths.get(fileName), out);
			out.flush();
		}

	}

	List<String> columnConfiguration() {
		String[] columns = {"FirstName", "LastName", "Email", "Pin Code", "yes", "column", "column", "column", "column", "Channel"};
		return Arrays.asList(columns);
	}

	@Override
	public List<List<Object>> populateData() {
		Object[] arr = {"FirstName", "LastName", "fakemail@mock,.com", 12345, "yes", "XH4234FSD", 4334.00, "yFone 5 XS", new Date(), "WEB"};
		List<List<Object>> rows = new ArrayList<>();
		for (int row = 0; row < 100; row++) {
			List<Object> modifiableList = new ArrayList(Arrays.asList(arr));
			rows.add(modifiableList);
		}
		return rows;
	}
}
