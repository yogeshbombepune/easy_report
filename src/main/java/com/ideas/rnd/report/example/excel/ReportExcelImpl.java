package com.ideas.rnd.report.example.excel;

import com.ideas.rnd.report.algo.ExcelReportGenerator;
import com.ideas.rnd.report.model.excel.Cell;
import com.ideas.rnd.report.model.excel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class ReportExcelImpl implements ReportExcel {

	@Override
	public void export(String fileName) throws IOException {
		Map dataMap = new LinkedHashMap();
		List<List<Cell>> columns = columnConfiguration();
		List<List<Cell>> dataSet = populateData();
		Workbook workbook = new XSSFWorkbook();
		ExcelReportGenerator excelReportGenerator = new ExcelReportGenerator(workbook, columns, dataSet);
		excelReportGenerator.generate();
		OutputStream fileOut;
		if (dataMap.containsKey("outputStream")) {
			fileOut = (OutputStream) dataMap.get("outputStream");
		} else {
			fileOut = new FileOutputStream(fileName);
		}
		workbook.write(fileOut);
	}

	public List<List<Cell>> columnConfiguration() {
		Cell[] columns = {Cell.builder().value("FirstName").font(Font.builder().bold(true).build()).build(), Cell.builder().value("LastName").font(Font.builder().bold(true).build()).build(), Cell.builder().value("Email").font(Font.builder().bold(true).build()).build(), Cell.builder().value("Pin Code").font(Font.builder().bold(true).build()).build(), Cell.builder().value("yes").font(Font.builder().bold(true).build()).build(), Cell.builder().value("column").font(Font.builder().bold(true).build()).build(), Cell.builder().value("column").font(Font.builder().bold(true).build()).build(), Cell.builder().value("column").font(Font.builder().bold(true).build()).build(), Cell.builder().value("column").font(Font.builder().bold(true).build()).build(), Cell.builder().value("Channel").font(Font.builder().bold(true).build()).build()};
		List<Cell> cells = Arrays.asList(columns);
		List<List<Cell>> lists = new ArrayList<>();
		lists.add(cells);
		return lists;
	}

	@Override
	public List<List<Cell>> populateData() {
		Cell[] arr = {Cell.builder().value("FirstName").build(), Cell.builder().value("LastName").build(), Cell.builder().value("fakemail@mock,.com").build(), Cell.builder().value(12345).build(), Cell.builder().value("yes").build(), Cell.builder().value("XH4234FSD").build(), Cell.builder().value(4334.00).build(), Cell.builder().value("yFone 5 XS").build(), Cell.builder().value(new Date()).build(), Cell.builder().value("WEB").build()};
		List<List<Cell>> rows = new ArrayList<>();
		for (int row = 0; row < 100; row++) {
			List<Cell> modifiableList = new ArrayList(Arrays.asList(arr));
			rows.add(modifiableList);
		}
		return rows;
	}
}
