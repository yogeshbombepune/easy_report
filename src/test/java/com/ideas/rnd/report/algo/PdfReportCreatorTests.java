package com.ideas.rnd.report.algo;

import com.ideas.rnd.report.example.pdf.ReportPdf;
import com.ideas.rnd.report.example.pdf.ReportPdfImpl;
import com.ideas.rnd.report.model.pdf.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfReportCreatorTests {

	private PdfReportCreator pdfReportGenerator;
	private PDFont pdFont;

	@Before
	public void setup() {

		List<Column> columnConfiguration = columnConfiguration();

		List<List<String>> populateData = populateData();

		Table table = getTable(columnConfiguration, populateData);
		List<Table> tables = new ArrayList<>();
		tables.add(table);
		tables.add(table);

		try {
			PDDocument doc = new PDDocument();
			this.pdFont = ReportPdf.loadFont(doc);
			this.pdfReportGenerator = new PdfReportCreator(doc, pdFont, headerConfiguration(),
					footerConfiguration(), tables, getGraphs());
			this.pdfReportGenerator.generate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getTextWidthTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method getTextWidth = pdfReportGenerator.getClass().getDeclaredMethod("getTextWidth", PDFont.class, String.class, float.class);
		getTextWidth.setAccessible(true);
		int textWidth = (int) getTextWidth.invoke(pdfReportGenerator, this.pdFont, "Yogesh", 10f);
		Assert.assertEquals(33, textWidth);
	}

	@Test
	public void getRightXTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method getRightX = pdfReportGenerator.getClass().getDeclaredMethod("getRightX", float.class, float.class, int.class);
		getRightX.setAccessible(true);
		float cellWidth = 150;
		float cellMargin = 2;
		int textWidth = 33;
		float xCoordinate = (float) getRightX.invoke(pdfReportGenerator, cellWidth, cellMargin, textWidth);
		Assert.assertEquals(113, xCoordinate, 0);
	}

	@Test
	public void getAdjustXTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method getAdjustX = pdfReportGenerator.getClass().getDeclaredMethod("getAdjustX", Alignment.class, float.class, float.class, int.class);
		getAdjustX.setAccessible(true);
		Alignment alignment = Alignment.LEFT;
		float cellWidth = 150;
		float cellMargin = 2;
		int textWidth = 33;
		float xCoordinate = (float) getAdjustX.invoke(pdfReportGenerator, alignment, cellWidth, cellMargin, textWidth);
		if (alignment == Alignment.CENTER) {
			Assert.assertEquals(57.5, xCoordinate, 0);
		} else if (alignment == Alignment.RIGHT) {
			Assert.assertEquals(113, xCoordinate, 0);
		} else {
			Assert.assertEquals(2, xCoordinate, 0);
		}
	}


	@Test
	public void getCenterXTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method getCenterX = pdfReportGenerator.getClass().getDeclaredMethod("getCenterX", float.class, float.class, int.class);
		getCenterX.setAccessible(true);
		float cellWidth = 150;
		float cellMargin = 2;
		int textWidth = 33;
		float xCoordinate = (float) getCenterX.invoke(pdfReportGenerator, cellWidth, cellMargin, textWidth);
		Assert.assertEquals(57.5, xCoordinate, 0);
	}

	@Test
	public void wrapLineAdjustmentChangeTest() throws NoSuchFieldException, IllegalAccessException {
		Field wrapLineAdjustment = pdfReportGenerator.getClass().getDeclaredField("wrapLineAdjustment");
		wrapLineAdjustment.setAccessible(true);
		int wrapLineAdjustmentActual = (int) wrapLineAdjustment.get(pdfReportGenerator);
		Assert.assertNotEquals(0, wrapLineAdjustmentActual);
	}

	@Test
	public void totalNumberOfPagesChangeTest() throws NoSuchFieldException, IllegalAccessException {
		Field totalNumberOfPages = pdfReportGenerator.getClass().getDeclaredField("totalNumberOfPages");
		totalNumberOfPages.setAccessible(true);
		int totalNumberOfPagesActual = (int) totalNumberOfPages.get(pdfReportGenerator);
		Assert.assertNotEquals(0, totalNumberOfPagesActual);
	}

	@Test
	public void getRangesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method getCenterX = pdfReportGenerator.getClass().getDeclaredMethod("getRanges", boolean.class);
		getCenterX.setAccessible(true);
		List<List<Range>> ranges = (List<List<Range>>) getCenterX.invoke(pdfReportGenerator, true);
		int actualSize = ranges.get(0).size();
		Assert.assertEquals(1, actualSize);
	}

	private Footer footerConfiguration() {
		URL resource = ReportPdf.class.getClassLoader().getResource("images/logo.png");
		assert resource != null;
		return Footer.builder()
				.lineColor(ReportPdf.PAGE_FOOTER_LINE_COLOR)
				.lineWidth(ReportPdf.PAGE_FOOTER_LINE_FONT_SIZE)
				.pageNumberPhrase(ReportPdf.PAGE_FOOTER_PAGE_NUMBER_PHRASE)
				.pageNumberPhraseColor(ReportPdf.PAGE_FOOTER_PAGE_NUMBER_PHRASE_COLOR)
				.pageNumberPhraseFont(ReportPdf.PAGE_FOOTER_PAGE_NUMBER_PHRASE_FONT)
				.pageNumberPhraseSize(ReportPdf.PAGE_FOOTER_PAGE_NUMBER_PHRASE_FONT_SIZE)
				.logoImage(new File(resource.getPath()))
				.build();
	}

	private Table getTable(List<Column> columns, List<List<String>> content) {
		return Table.builder()
				.cellPadding(ReportPdf.TABLE_CELL_PADDING)
				.columns(columns)
				.columnHeight(ReportPdf.TABLE_COLUMN_HEIGHT * 3)
				.fixedColumns(fixedColumnRangeConfiguration())
				.content(content)
				.numberOfRows(null != content ? content.size() : 0)
				.rowHeight(ReportPdf.TABLE_ROW_HEIGHT)
				.margin(ReportPdf.MARGIN)
				.pageSize(ReportPdf.PAGE_SIZE)
				.isLandscape(ReportPdf.IS_LANDSCAPE)
				.lineColor(ReportPdf.TABLE_LINE_COLOR)
				.lineWidth(ReportPdf.TABLE_LINE_WIDTH)
				.headerTextFont(ReportPdf.TABLE_HEADER_TEXT_FONT)
				.headerFontSize(ReportPdf.TABLE_HEADER_FONT_SIZE)
				.headerBackgroundColor(ReportPdf.TABLE_HEADER_BACKGROUND_COLOR)
				.headerTextColor(ReportPdf.TABLE_HEADER_TEXT_COLOR)
				.contentTextFont(ReportPdf.TABLE_CONTENT_TEXT_FONT)
				.contentFontSize(ReportPdf.TABLE_CONTENT_FONT_SIZE)
				.contentTextColor(ReportPdf.TABLE_CONTENT_TEXT_COLOR)
				.columnWordWrapEnable(false)
				.columnSplitEnable(true)
				.splitRegex(ReportPdf.TABLE_HEADER_SPLIT_REGEX_WITH_PIPE)
				.build();
	}

	private Header headerConfiguration() {
		return Header.builder()
				.clientName("Moevenpick Amsterdam")
				.clientNameColor(ReportPdf.PAGE_HEADER_PROPERTY_NAME_COLOR)
				.clientNameFont(ReportPdf.PAGE_HEADER_PROPERTY_NAME_FONT)
				.clientNameFontSize(ReportPdf.PAGE_HEADER_PROPERTY_NAME_SIZE)
				.reportName("Last Room Value report")
				.reportNameColor(ReportPdf.PAGE_HEADER_REPORT_NAME_COLOR)
				.reportNameFont(ReportPdf.PAGE_HEADER_REPORT_NAME_FONT)
				.reportNameFontSize(ReportPdf.PAGE_HEADER_REPORT_NAME_FONT_SIZE)
				.metaKeyVal(getHeaderMap())
				.metaKeyValColor(ReportPdf.PAGE_HEADER_META_KEY_VAL_COLOR)
				.metaKeyValFont(ReportPdf.PAGE_HEADER_META_KEY_VAL_FONT)
				.metaKeyValFontSize(ReportPdf.PAGE_HEADER_META_KEY_VAL_FONT_SIZE)
				.build();
	}

	private Map<String, Object> getHeaderMap() {
		Map<String, Object> headerMap = new LinkedHashMap<>();
		headerMap.put("Printed By:", "ideas_adm");
		headerMap.put("Print Date: ", "Fri 02-Nov-2018 15:00");
		headerMap.put("Analysis Period :", "01-Jun-2014 to 30-Jun-2014");
		headerMap.put("Comparison Period :", "01-Jun-2013 to 30-Jun-2013");
		headerMap.put("Legend:", "Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date,Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date,Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date,Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date,Indicates, an, active, Hotel, Forecast, Override, on, this, date, Indicates, an, active, Hotel, Forecast, Override, on, this, date");
		return headerMap;
	}

	private List<Column> columnConfiguration() {
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

	public List<Range> fixedColumnRangeConfiguration() {
		List<Range> fixedColumns = new ArrayList<>();
		Range range = new Range();
		range.setFrom(0);
		range.setTo(1);
		fixedColumns.add(range);
		return fixedColumns;
	}

	private List<List<String>> populateData() {
		String[] arr = {"FirstName", "LastName", "fakemail@mock.com", "12345", "yes", "XH4234FSD", "4334", "yFone 5 XS", "31/05/2013 07:15 am", "WEB"};
		List<List<String>> rows = new ArrayList<>();
		for (int row = 0; row < 100; row++) {
			List<String> modifiableList = new ArrayList(Arrays.asList(arr));
			rows.add(modifiableList);
		}
		return rows;
	}

	private Graph getGraphs() {
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
		graphs.add(new File(resource13.getPath()));
		return new Graph(graphs);
	}
}
