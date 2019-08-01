package com.ideas.rnd.report.example.pdf;

import com.ideas.rnd.report.algo.PdfReportCreator;
import com.ideas.rnd.report.model.pdf.*;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
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

		List<Table> tables = new ArrayList<>();
		tables.add(table);
		tables.add(table);

		PDDocument doc2 = new PDDocument();
		File temp = File.createTempFile("temp-file-name", ".report");
		doc2.save(temp.getAbsoluteFile());

		System.out.println(temp.getAbsoluteFile());
		try (PDDocument doc1 = PDDocument.load(temp, MemoryUsageSetting.setupTempFileOnly(10240000))) {
			PDFont pdFont = ReportPdf.loadFont(doc1);
			PdfReportCreator pdfReportGenerator = new PdfReportCreator(doc1, pdFont, headerConfiguration(),
					footerConfiguration(), tables, getGraphs());
			pdfReportGenerator.generate();
			// Define the length of the encryption key.
			// Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
			int keyLength = 128;

			AccessPermission ap = new AccessPermission();

			// Disable printing, everything else is allowed
			ap.setCanPrint(false);

			// Owner password (to open the file with all permissions) is "12345"
			// User password (to open the file but with restricted permissions, is empty here)
			StandardProtectionPolicy spp = new StandardProtectionPolicy("12345", "12345", ap);
			spp.setEncryptionKeyLength(keyLength);
			spp.setPermissions(ap);
			doc1.protect(spp);
			doc1.save(fileName);
			temp.deleteOnExit();
		}

	}

	private Table getTable(List<Column> columns, List<List<String>> content) {
		return Table.builder()
				.cellPadding(TABLE_CELL_PADDING)
				.columns(columns)
				.columnHeight(TABLE_COLUMN_HEIGHT * 3)
				.fixedColumns(fixedColumnRangeConfiguration())
				.content(content)
				.numberOfRows(null != content ? content.size() : 0)
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
		headerMap.put("Analysis Period :", "01-Jun-2014 to 30-Jun-2014");
		headerMap.put("Comparison Period :", "01-Jun-2013 to 30-Jun-2013");
		headerMap.put("Legend:", "Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date,Indicates, an, active, Hotel");
		return headerMap;
	}

	@Override
	public List<Column> columnConfiguration() {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder()
				.name("FirstName1|FirstName2|FirstName3")
				.width(50)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.CENTER)
				.build());
		columns.add(Column.builder()
				.name("LastName")
				.width(50)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.CENTER)
				.build());
		columns.add(Column.builder()
				.name("Email")
				.width(150)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.RIGHT)
				.build());
		columns.add(Column.builder()
				.name("ZipCode")
				.width(43)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.RIGHT)
				.build());
		columns.add(Column.builder()
				.name("MailOptIn")
				.width(50)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.RIGHT)
				.build());
		columns.add(Column.builder()
				.name("Code")
				.width(80)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.RIGHT)
				.build());
		columns.add(Column.builder()
				.name("Branch")
				.width(50)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.RIGHT)
				.build());
		columns.add(Column.builder()
				.name("Product")
				.width(80)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.RIGHT)
				.build());
		columns.add(Column.builder()
				.name("Date")
				.width(120)
				.headerAlignment(Alignment.CENTER)
				.contentAlignment(Alignment.RIGHT)
				.build());
		columns.add(Column.builder()
				.name("Channel")
				.width(43)
				.headerAlignment(Alignment.CENTER)
				//.contentAlignment(Alignment.RIGHT)
				.build());
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
		for (int row = 0; row < 1000; row++) {
			List<String> modifiableList = new ArrayList(Arrays.asList(arr));
			rows.add(modifiableList);
		}
		return rows;
	}

	@Override
	public Graph getGraphs() {
		URL resource1 = ReportPdfImpl.class.getClassLoader().getResource("images/graph1.jpg");
		URL resource2 = ReportPdfImpl.class.getClassLoader().getResource("images/graph2.png");
		URL resource3 = ReportPdfImpl.class.getClassLoader().getResource("images/graph3.png");
		URL resource4 = ReportPdfImpl.class.getClassLoader().getResource("images/graph4.png");
		URL resource5 = ReportPdfImpl.class.getClassLoader().getResource("images/graph5.png");
		URL resource6 = ReportPdfImpl.class.getClassLoader().getResource("images/graph6.png");
		URL resource7 = ReportPdfImpl.class.getClassLoader().getResource("images/graph7.png");
		URL resource8 = ReportPdfImpl.class.getClassLoader().getResource("images/graph8.png");
		URL resource9 = ReportPdfImpl.class.getClassLoader().getResource("images/graph9.png");
		URL resource10 = ReportPdfImpl.class.getClassLoader().getResource("images/graph10.PNG");
		URL resource11 = ReportPdfImpl.class.getClassLoader().getResource("images/graph11.png");
		URL resource12 = ReportPdfImpl.class.getClassLoader().getResource("images/graph12.png");
		URL resource13 = ReportPdfImpl.class.getClassLoader().getResource("images/PcrChartImageFogX7eRH4c155195530067.png");
		List<File> graphs = new ArrayList<>();
		graphs.add(new File(resource1.getPath()));
		graphs.add(new File(resource2.getPath()));
		graphs.add(new File(resource3.getPath()));
		graphs.add(new File(resource4.getPath()));
		graphs.add(new File(resource5.getPath()));
		graphs.add(new File(resource6.getPath()));
		graphs.add(new File(resource7.getPath()));
		graphs.add(new File(resource8.getPath()));
		graphs.add(new File(resource9.getPath()));
		graphs.add(new File(resource10.getPath()));
		graphs.add(new File(resource11.getPath()));
		graphs.add(new File(resource12.getPath()));
		graphs.add(new File("C:\\Users\\idnyob\\Desktop\\PcrChartImageFogX7eRH4c155195530067.png"));
		return new Graph(graphs);
	}
}
