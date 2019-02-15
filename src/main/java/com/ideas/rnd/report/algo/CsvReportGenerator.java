package com.ideas.rnd.report.algo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class CsvReportGenerator {
	//Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	//CSV file header
	private List<String> fileHeader;
	private String fileName;
	private List<List<Object>> dataSet;

	private CsvReportGenerator() {
	}

	public CsvReportGenerator(String fileName, List<String> fileHeader, List<List<Object>> dataSet) {
		this.fileName = fileName;
		this.fileHeader = fileHeader;
		this.dataSet = dataSet;
	}

	public void generate() throws IOException {
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(this.fileName);

			//Write the CSV file header
			fileWriter.append(String.join(COMMA_DELIMITER, this.fileHeader));

			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a new object list to the CSV file
			for (List<Object> rows : dataSet) {
				int cellNumber = 0;
				for (Object cell : rows) {
					Object cellValue = evaluate(cell);
					fileWriter.append((String) cellValue);
					cellNumber++;
					if (cellNumber == rows.size()) {
						fileWriter.append(NEW_LINE_SEPARATOR);
					} else {
						fileWriter.append(COMMA_DELIMITER);
					}
				}
			}
			System.out.println("CSV file was created successfully !!!");
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
	}

	private Object evaluate(Object cell) {
		Object o;
		if (cell instanceof String) {
			String c = (String) cell;
			if (c.contains(COMMA_DELIMITER)) {
				StringBuilder builder = new StringBuilder();
				builder.append("\"");
				builder.append(c);
				builder.append("\"");
				o = builder.toString();
			} else {
				o = c;
			}
		} else if (cell instanceof Integer) {
			o = String.valueOf(cell);
		} else if (cell instanceof Double) {
			o = String.valueOf(cell);
		} else if (cell instanceof Long) {
			o = String.valueOf(cell);
		} else if (cell instanceof Date) {
			o = String.valueOf(cell);
		} else if (cell instanceof Float) {
			o = String.valueOf(cell);
		} else if (cell instanceof Character) {
			o = String.valueOf(cell);
		} else {
			o = String.valueOf(cell);
		}
		return o;
	}
}
