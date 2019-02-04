package com.ideas.rnd.pdf;

import com.ideas.rnd.pdf.algo.PdfReportGenerator;
import com.ideas.rnd.pdf.model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ReportPdfImpl implements ReportPdf {

	@Override
	public void export(String fileName) throws Exception {

		List<Column> columnConfiguration = columnConfiguration();

		List<List<String>> populateData = populateData();

		Table table = getTable(columnConfiguration, populateData);

		try (PDDocument doc = new PDDocument()) {
			PDFont pdFont = ReportPdf.loadFont(doc);

			PdfReportGenerator pdfReportGenerator = new PdfReportGenerator(doc, pdFont, headerConfiguration(),
					footerConfiguration(), table, getGraphs());

			pdfReportGenerator.getPDF();

			doc.save(fileName);
		}

	}

	private Table getTable(List<Column> columns, List<List<String>> content) {
		float totalRowWidth = (float) columns.stream().mapToDouble(Column::getWidth).sum();
		return Table.builder()
				.cellPadding(TABLE_CELL_PADDING)
				.columns(columns)
				.columnHeight(TABLE_COLUMN_HEIGHT * 3)
				.fixedColumns(fixedColumnRangeConfiguration())
				.content(content)
				.rowWidth(totalRowWidth)
				.numberOfRows(content.size())
				.rowHeight(TABLE_ROW_HEIGHT)
				.margin(MARGIN)
				.pageSize(PAGE_SIZE)
				.isLandscape(IS_LANDSCAPE)
				.lineColor(TABLE_LINE_COLOR)
				.lineWidth(TABLE_LINE_WIDTH)
				.headerTextFont(TABLE_HEADER_TEXT_FONT)
				.headerFontSize(TABLE_HEADER_FONT_SIZE)
				.headerBackgroundColor(TABLE_HEADER_BACKGROUND_COLOR)
				.headerTextColor(TABLE_HEADER_TEXT_COLOR)
				.contentTextFont(TABLE_CONTENT_TEXT_FONT)
				.contentFontSize(TABLE_CONTENT_FONT_SIZE)
				.contentTextColor(TABLE_CONTENT_TEXT_COLOR)
				.columnWordWrapEnable(false)
				.columnSplitEnable(true)
				.splitRegex(TABLE_HEADER_SPLIT_REGEX_WITH_PIPE)
				.build();
	}

	@Override
	public Header headerConfiguration() {
		return Header.builder()
				.propertyName("Moevenpick Amsterdam")
				.propertyNameColor(PAGE_HEADER_PROPERTY_NAME_COLOR)
				.propertyNameFont(PAGE_HEADER_PROPERTY_NAME_FONT)
				.propertyNameFontSize(PAGE_HEADER_PROPERTY_NAME_SIZE)
				.reportName("Last Room Value report")
				.reportNameColor(PAGE_HEADER_REPORT_NAME_COLOR)
				.reportNameFont(PAGE_HEADER_REPORT_NAME_FONT)
				.reportNameFontSize(PAGE_HEADER_REPORT_NAME_FONT_SIZE)
				.metaKeyVal(getHeaderMap())
				.metaKeyValColor(PAGE_HEADER_META_KEY_VAL_COLOR)
				.metaKeyValFont(PAGE_HEADER_META_KEY_VAL_FONT)
				.metaKeyValFontSize(PAGE_HEADER_META_KEY_VAL_FONT_SIZE)
				.build();
	}

	private Map<String, Object> getHeaderMap() {
		Map<String, Object> headerMap = new LinkedHashMap<>();
		headerMap.put("Printed By:", "ideas_adm");
		headerMap.put("Print Date: ", "Fri 02-Nov-2018 15:00");
		headerMap.put("Start Date:", "Sun 29-Jun-2014");
		headerMap.put("End Date:", "Tue 29-Jun-2014");
		headerMap.put("Legend:", "(*) Indicates an active Hotel Forecast Override on this date");
		return headerMap;
	}

	@Override
	public List<Column> columnConfiguration() {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().name("FirstName1|FirstName2|FirstName3").width(50).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("LastName").width(50).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("电子邮件").width(150).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("ZipCode").width(43).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("MailOptIn").width(50).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("Code").width(80).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("Branch").width(50).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("Product").width(80).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("Date").width(120).alignment(Alignment.CENTER).build());
		columns.add(Column.builder().name("Channel").width(43).alignment(Alignment.CENTER).build());
		return columns;
	}

	@Override
	public List<Range> fixedColumnRangeConfiguration() {
		List<Range> fixedColumns = new ArrayList<>();
		Range range = new Range();
		range.setFrom(0);
		range.setTo(1);
		fixedColumns.add(range);
		return fixedColumns;
	}

	@Override
	public List<List<String>> populateData() {
		String[] arr = {"FirstName", "LastName", "fakemail@mock.com", "12345", "yes", "XH4234FSD", "4334", "yFone 5 XS", "31/05/2013 07:15 am", "WEB"};
		List<List<String>> rows = new ArrayList<>();
		for (int row = 0; row < 100; row++) {
			List<String> modifiableList = new ArrayList(Arrays.asList(arr));
			rows.add(modifiableList);
		}
		return rows;
	}

	@Override
	public Graph getGraphs() {
		URL resource1 = ReportPdfImpl.class.getClassLoader().getResource("images/graph1.jpg");
		URL resource2 = ReportPdfImpl.class.getClassLoader().getResource("images/graph2.png");
		List<File> graphs = new ArrayList<>();
		assert resource1 != null;
		graphs.add(new File(resource1.getPath()));
		assert resource2 != null;
		graphs.add(new File(resource2.getPath()));
		return new Graph(graphs);
	}
}
