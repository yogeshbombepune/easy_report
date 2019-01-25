package com.ideas.rnd.pdf;

import com.google.common.collect.ObjectArrays;
import com.ideas.rnd.pdf.model.Column;
import com.ideas.rnd.pdf.model.Range;
import com.ideas.rnd.pdf.model.Table;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

public class PDFTableGenerator {
	// Generates document from Table object
	public void generatePDF(Table table) throws IOException {
		PDDocument doc = null;
		try {
			doc = new PDDocument();
			drawTable(doc, table);
			addGraphs(doc, table);
			addFooter(doc, table);
			doc.save("results.pdf");
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	private void addGraphs(PDDocument doc, Table table) throws IOException {
		float totalHeightForGraph = table.getPageSize().getHeight() - table.getTopMargin() - table.getMargin();
		float totalWidthForGraph = table.getPageSize().getWidth() - (2 * table.getMargin());
		float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getTopMargin() : table.getPageSize().getHeight() - table.getTopMargin();
		PDPage page = generatePage(doc, table);
		PDPageContentStream contentStream = generateContentStream(doc, page, table);
		addHeader(contentStream, table);
		URL resource = getClass().getClassLoader().getResource("images/graph1.jpg");
		PDImageXObject pdImage = PDImageXObject.createFromFile(resource.getPath(), doc);
		float height = 0;
		float width = 0;
		if (pdImage.getHeight() > totalHeightForGraph) {
			height = totalHeightForGraph;
		} else {
			height = pdImage.getHeight();
		}
		if (pdImage.getWidth() > totalWidthForGraph) {
			width = totalWidthForGraph;
		} else {
			width = pdImage.getWidth();
		}
		tableTopY -= height;
		contentStream.drawImage(pdImage, table.getPageSize().getLowerLeftX() + table.getMargin(), tableTopY, width, height);
		contentStream.close();
	}

	private float addHeader(PDPageContentStream contentStream, Table table) throws IOException {
		float xPos = table.getMargin();
		float totalWidth = table.getPageSize().getWidth() - (table.getMargin() * 2);
		float cellWidth = totalWidth / 4;
		float pageTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getMargin() : table.getPageSize().getHeight() - table.getMargin();
		Map<String, Object> headerMap = getHeaderMap();

		int line = (int) Math.ceil((float) (headerMap.size() - 2) / 2);
		float heightForHeaderBackground = line * table.getRowHeight();
		drawCellBackground(contentStream, table.getMargin(), pageTopY - 3 - (table.getRowHeight() * 4), totalWidth, heightForHeaderBackground, Color.LIGHT_GRAY);

		contentStream.setLineWidth(0.2f);
		String[] label = {"Moevenpick Amsterdam", "Last Room Value report"};
		float nextY = pageTopY;

		for (int i = 0; i < 2; i++) {
			nextY = drawFirstTwoHeaderLines(contentStream, table, label[i], nextY);
		}

		Set<Map.Entry<String, Object>> entries = headerMap.entrySet();
		int flag = 0;
		for (Map.Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			Object value = entry.getValue();

			contentStream.beginText();
			contentStream.newLineAtOffset(xPos, nextY);
			contentStream.showText(key);
			contentStream.endText();

			xPos += cellWidth;

			contentStream.beginText();
			contentStream.newLineAtOffset(xPos, nextY);
			contentStream.showText(value.toString());
			contentStream.endText();

			xPos += cellWidth;

			if ((flag % 2) != 0) {
				nextY -= table.getRowHeight();
				xPos = table.getMargin();
			}
			flag++;
		}


		return nextY;
	}


	private void drawCellBackground(PDPageContentStream contentStream, final float startX, final float startY, final float width, final float height, Color color)
			throws IOException {
		contentStream.setNonStrokingColor(color);

		contentStream.addRect(startX, startY, width, height);
		contentStream.fill();
		contentStream.closePath();

		// Reset NonStrokingColor to default value
		contentStream.setNonStrokingColor(Color.BLACK);
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

	private float drawFirstTwoHeaderLines(PDPageContentStream contentStream, Table table, String label, float nextY) throws IOException {
		contentStream.beginText();
		contentStream.setFont(PDType1Font.TIMES_ROMAN, 8);
		int centerX = Math.round((table.getPageSize().getWidth() / 2) - (label.length() / 2));
		contentStream.newLineAtOffset(centerX, nextY);
		contentStream.showText(label);
		contentStream.endText();
		nextY -= table.getRowHeight();
		return nextY;
	}

	private void addFooter(PDDocument doc, Table table) throws IOException {
		int numberOfPages = doc.getNumberOfPages();
		for (int pageNumber = 0; pageNumber < numberOfPages; pageNumber++) {
			PDPage page = doc.getPage(pageNumber);
			PDPageContentStream footerContentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
			drawHorizontalLine(table, doc, footerContentStream);
			drawLeftSection(table, doc, footerContentStream);
			drawRightSection(table, numberOfPages, pageNumber + 1, footerContentStream);
			footerContentStream.close();
		}
	}

	private void drawHorizontalLine(Table table, PDDocument doc, PDPageContentStream footerContentStream) throws IOException {
		footerContentStream.setStrokingColor(Color.LIGHT_GRAY);
		footerContentStream.setLineWidth(0.6f);
		float yCoordinate = table.getPageSize().getLowerLeftY() + table.getMargin();
		float startX = table.getPageSize().getLowerLeftX() + table.getMargin();
		float endX = table.getPageSize().getUpperRightX() - table.getMargin();
		footerContentStream.moveTo(startX, yCoordinate);
		footerContentStream.lineTo(endX, yCoordinate);
		footerContentStream.stroke();
		//Reset
		footerContentStream.setStrokingColor(Color.BLACK);
	}

	private void drawLeftSection(Table table, PDDocument doc, PDPageContentStream footerContentStream) throws IOException {
		//URL resource = getClass().getClassLoader().getResource("images" + System.getProperty("file.separator") + "logo.png");
		URL resource = getClass().getClassLoader().getResource("images/logo.png");
		PDImageXObject pdImage = PDImageXObject.createFromFile(resource.getPath(), doc);
		footerContentStream.drawImage(pdImage, table.getPageSize().getLowerLeftX() + table.getMargin(), table.getPageSize().getLowerLeftY() + 20, 30, 15);
	}

	private void drawRightSection(Table table, int numberOfPages, int pageNumber, PDPageContentStream footerContentStream) throws IOException {
		footerContentStream.beginText();
		footerContentStream.setFont(PDType1Font.TIMES_ROMAN, 8);
		footerContentStream.newLineAtOffset(table.getPageSize().getUpperRightX() - table.getMargin() - 40, table.getPageSize().getLowerLeftY() + 20);
		footerContentStream.showText("Page " + pageNumber + " of " + numberOfPages);
		footerContentStream.endText();
	}

	// Configures basic setup for the table and draws it page by page
	public void drawTable(PDDocument doc, Table table) throws IOException {
		// Calculate pagination
		Integer rowsPerPage = null;
		Integer numberOfPages = null;
		float pageWidth = table.getPageSize().getWidth();
		float totalRowWidth = table.getRowWidth();
		rowsPerPage = getRowsPerPage(table);
		List<List<Range>> rangesOfColumnRangePerPage;
		numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();
		List<Range> fixedColumns = table.getFixedColumns();
		boolean isFixedColumn = fixedColumns != null && fixedColumns.size() > 0;
		if (pageWidth < totalRowWidth) {
			rangesOfColumnRangePerPage = getRangesOfColumnRangePerPageNew(table, isFixedColumn);
		} else {
			rangesOfColumnRangePerPage = getSinglePageRange(table);
		}

		// Generate each page, get the content and draw it
		for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
			for (List<Range> range : rangesOfColumnRangePerPage) {
				PDPage page = generatePage(doc, table);
				PDPageContentStream contentStream = generateContentStream(doc, page, table);
				addHeader(contentStream, table);
				List<List<String>> currentPageContent = getContentForCurrentPage(table, rowsPerPage, pageCount, range);
				drawCurrentPage(table, currentPageContent, contentStream, range, isFixedColumn);
			}
		}
	}

	private List<List<Range>> getSinglePageRange(Table table) {
		List<List<Range>> lists = new ArrayList<>();

		List<Range> ranges = new ArrayList<>();
		float totalWidth = 0;
		Integer numberOfColumns = table.getNumberOfColumns();
		for (int i = 0; i < numberOfColumns; i++) {
			totalWidth += table.getColumns().get(i).getWidth();
		}

		Range range = new Range();
		range.setFrom(0);
		range.setTo(numberOfColumns - 1);
		range.setOffSet(totalWidth);
		ranges.add(range);
		lists.add(ranges);
		return lists;
	}

	private List<Range> getRangesOfColumnRangePerPage(Table table) {
		float totalWidth = table.getPageSize().getWidth();
		float xPos = totalWidth;
		List<Range> ranges = new ArrayList<>();
		int count = 0;
		float columnWidth = 0;
		float lastOffset = 0;
		for (int i = 0; i < table.getNumberOfColumns(); i++) {
			float width = table.getColumns().get(i).getWidth();
			xPos -= width;
			columnWidth += width;
			int start = i - count;
			if (xPos < 0) {
				count = 0;
				int end = --i;
				Range range = new Range();
				range.setFrom(start);
				range.setTo(end);
				lastOffset = columnWidth - width;
				range.setOffSet(lastOffset);
				ranges.add(range);
				xPos = totalWidth;
				columnWidth = 0;
			} else {
				count++;
			}
		}
		getLastOffset(ranges, table.getNumberOfColumns(), columnWidth);
		return ranges;

	}

	private List<List<Range>> getRangesOfColumnRangePerPageNew(Table table, boolean isFixedColumn) {
		List<List<Range>> listList = new ArrayList<>();
		float totalWidth = table.getPageSize().getWidth() - (table.getMargin() * 2);
		float xPos = totalWidth;

		int count = 0;
		float columnWidth = 0;
		float lastOffset = 0;
		float fixedColumnWidth = 0;
		List<Range> fixedColumns = table.getFixedColumns();
		if (isFixedColumn) {
			fixedColumnWidth = getOffset(table.getColumns(), fixedColumns.get(0).getFrom(), fixedColumns.get(0).getTo());
			fixedColumns.get(0).setOffSet(fixedColumnWidth);
		}
		List<Range> ranges = null;
		for (int i = 0; i < table.getNumberOfColumns(); i++) {

			int start = i - count;
			float width = table.getColumns().get(i).getWidth();

			if (start != 0 && count == 0 && isFixedColumn) {
				xPos -= width + fixedColumnWidth;
				columnWidth += width + fixedColumnWidth;
				ranges = new ArrayList<>();
				ranges.addAll(fixedColumns);
			} else {
				xPos -= width;
				columnWidth += width;
			}
			if (xPos < 0) {
				if (null == ranges) {
					ranges = new ArrayList<>();
				}
				count = 0;
				int end = --i;
				Range range = new Range();
				range.setFrom(start);
				range.setTo(end);
				lastOffset = columnWidth - width;
				range.setOffSet(lastOffset);
				ranges.add(range);
				xPos = totalWidth;
				columnWidth = 0;
				listList.add(ranges);
			} else {
				count++;
			}
		}
		getLastOffsetNew(listList, fixedColumns, isFixedColumn, table.getNumberOfColumns(), columnWidth);
		return listList;

	}

	private void getLastOffsetNew(List<List<Range>> listList, List<Range> fixedColumns, boolean isFixedColumn, Integer numberOfColumns, float lastOffset) {
		List<Range> ranges1 = listList.get(listList.size() - 1);
		int to = ranges1.get(ranges1.size() - 1).getTo();
		List<Range> ranges = new ArrayList<>();
		if (isFixedColumn) {
			ranges.addAll(fixedColumns);
		}

		Range range = new Range();
		range.setFrom(to + 1);
		range.setTo(numberOfColumns - 1);
		range.setOffSet(lastOffset);
		ranges.add(range);
		listList.add(ranges);
	}

	private float getOffset(List<Column> columns, int from, int to) {
		float fixedColumnWidth = 0;
		for (int i = from; i < to + 1; i++) {
			fixedColumnWidth += columns.get(i).getWidth();
		}
		return fixedColumnWidth;
	}

	private void getLastOffset(List<Range> ranges, Integer numberOfColumns, float lastOffset) {
		int to = ranges.get(ranges.size() - 1).getTo();
		Range range = new Range();
		range.setFrom(to + 1);
		range.setTo(numberOfColumns - 1);
		range.setOffSet(lastOffset);
		ranges.add(range);
	}

	private Integer getRowsPerPage(Table table) {
		Integer rowsPerPage;
		rowsPerPage = new Double(Math.floor(table.getHeight() / table.getRowHeight())).intValue() - 1; // subtract
		return rowsPerPage;
	}

	// Draws current page table grid and border lines and content
	private void drawCurrentPage(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, List<Range> ranges, boolean isFixedColumn)
			throws IOException {
		float nextX = 0;
		float nextTextXForNonZeroIndex = 0;
		for (Range range : ranges) {

			float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getTopMargin() : table.getPageSize().getHeight() - table.getTopMargin();

			// Draws grid and borders
			nextX = drawTableGrid(table, currentPageContent, contentStream, tableTopY, range, nextX, isFixedColumn);

			// Position cursor to start drawing content


			float nextTextX = table.getMargin() + table.getCellMargin();
			if (range.getFrom() != 0 && isFixedColumn) {
				nextTextX = nextTextXForNonZeroIndex;
			}


			// Calculate center alignment for text in cell considering font height
			float nextTextY = tableTopY - (table.getRowHeight() / 2)
					- ((table.getTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table.getFontSize()) / 4);

			// Write column headers
			String[] columnsNamesAsArray = table.getColumnsNamesAsArray(table.getColumns().subList(range.getFrom(), range.getTo() + 1));
			writeHeaderContentLine(columnsNamesAsArray, contentStream, nextTextX, nextTextY, table, range);
			nextTextY -= table.getRowHeight();
			nextTextX = table.getMargin() + table.getCellMargin();
			if (range.getFrom() != 0 && isFixedColumn) {
				nextTextX = nextTextXForNonZeroIndex;
			}

			// Write content
			for (int i = 0; i < currentPageContent.size(); i++) {
				Object[] objects = currentPageContent.get(i).toArray();
				writeContentLine((String[]) objects, contentStream, nextTextX, nextTextY, table, range, ranges.get(0).getTo(), isFixedColumn);
				nextTextY -= table.getRowHeight();
				if (range.getFrom() == 0) {
					nextTextX = table.getMargin() + table.getCellMargin();
				}
			}


			nextTextXForNonZeroIndex = nextTextX + ranges.get(0).getOffSet();


		}


		contentStream.close();
	}

	// Writes the content for one line
	private void writeContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
								  Table table, Range range, int to, boolean isFixedColumn) throws IOException {
		int from = range.getFrom();
		int i;
		int end = 0;
		if (from == 0) {
			i = 0;
			end = range.getTo() + 1;
		} else if (from != 0 && !isFixedColumn) {
			i = 0;
			end = lineContent.length;
		} else {
			i = to + 1;
			end = lineContent.length;
		}

		for (; i < end; i++) {
			String text = lineContent[i];
			contentStream.beginText();
			contentStream.newLineAtOffset(nextTextX, nextTextY);
			contentStream.showText(text != null ? text : "");
			contentStream.endText();
			nextTextX += table.getColumns().get(from++).getWidth();
		}
	}

	// Writes the content for one line
	private void writeHeaderContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
										Table table, Range range) throws IOException {
		contentStream.setNonStrokingColor(Color.white);
		int from = range.getFrom();
		for (int i = 0; i < lineContent.length; i++) {
			contentStream.beginText();
			contentStream.newLineAtOffset(nextTextX, nextTextY);
			String text = lineContent[i];
			contentStream.showText(text != null ? text : "");
			contentStream.endText();
			nextTextX += table.getColumns().get(from++).getWidth();
		}
		contentStream.setNonStrokingColor(Color.BLACK);
	}

	private float drawTableGrid(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, float tableTopY, Range range, float expNextX, boolean isFixedColumn)
			throws IOException {
		contentStream.setStrokingColor(Color.LIGHT_GRAY);
		contentStream.setLineWidth(0.5f);
		// Draw row lines
		float nextY = tableTopY;
		float nextX = table.getMargin();

		for (int column = range.getFrom(); column <= range.getTo(); column++) {
			float width = table.getColumns().get(column).getWidth();
			drawCellBackground(contentStream, nextX, nextY - table.getRowHeight(), width, table.getRowHeight(), new Color(2, 43, 87));
			nextX += width;
		}
		//Reset X
		nextX = table.getMargin();
		for (int i = 0; i <= currentPageContent.size() + 1; i++) {
			contentStream.moveTo(table.getMargin(), nextY);
			contentStream.lineTo(table.getMargin() + range.getOffSet(), nextY);
			contentStream.stroke();
			nextY -= table.getRowHeight();
		}

		// Draw column lines
		final float tableYLength = table.getRowHeight() + (table.getRowHeight() * currentPageContent.size());
		final float tableBottomY = tableTopY - tableYLength;

		if (range.getFrom() != 0 && isFixedColumn) {
			nextX = expNextX;
		}
		for (int i = range.getFrom(); i < range.getTo() + 1; i++) {
			contentStream.moveTo(nextX, tableTopY);
			contentStream.lineTo(nextX, tableBottomY);
			contentStream.stroke();
			nextX += table.getColumns().get(i).getWidth();
		}
		contentStream.moveTo(nextX, tableTopY);
		contentStream.lineTo(nextX, tableBottomY);
		contentStream.stroke();
		contentStream.setStrokingColor(Color.BLACK);
		return nextX;
	}

	private List<List<String>> getContentForCurrentPage(Table table, Integer rowsPerPage, int pageCount, List<Range> ranges) {
		List<List<String>> newContentList = new ArrayList<>();

		int startRange = pageCount * rowsPerPage;
		int endRange = (pageCount * rowsPerPage) + rowsPerPage;
		if (endRange > table.getNumberOfRows()) {
			endRange = table.getNumberOfRows();
		}
		String[][] content = table.getContent();
		for (int i = startRange; i < endRange; i++) {
			String[] commonDataChunk = Arrays.copyOfRange(content[i], ranges.get(0).getFrom(), ranges.get(0).getTo() + 1);
			if (ranges.size() > 1) {
				String[] variableChunkOfData = Arrays.copyOfRange(content[i], ranges.get(1).getFrom(), ranges.get(1).getTo() + 1);
				commonDataChunk = ObjectArrays.concat(commonDataChunk, variableChunkOfData, String.class);
			}
			newContentList.add(Arrays.asList(commonDataChunk));
		}

		return newContentList;
	}

	private PDPage generatePage(PDDocument doc, Table table) {
		PDPage page = new PDPage();
		page.setMediaBox(table.getPageSize());
		page.setRotation(table.isLandscape() ? 90 : 0);
		doc.addPage(page);
		return page;
	}

	private PDPageContentStream generateContentStream(PDDocument doc, PDPage page, Table table) throws IOException {
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false);
		if (table.isLandscape()) {
			Matrix matrix = new Matrix(0, 1, -1, 0, table.getPageSize().getWidth(), 0);
			contentStream.transform(matrix);
		}
		contentStream.setFont(table.getTextFont(), table.getFontSize());
		return contentStream;
	}
}
