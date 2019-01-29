package com.ideas.rnd.pdf;

import com.google.common.collect.ObjectArrays;
import com.ideas.rnd.pdf.model.*;
import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

class PDFTableGenerator {
	private Table table;
	private Header header;

	// Generates document from Table object
	void generatePDF(Header header, Table table) throws IOException {
		try (PDDocument doc = new PDDocument()) {
			this.header = header;
			this.table = table;
			drawTable(doc);
			addGraphs(doc);
			addFooter(doc);
			doc.save("results.pdf");
		}
	}

	private void addGraphs(PDDocument doc) throws IOException {
		float totalHeightForGraph = table.getPageSize().getHeight() - table.getTopMargin() - table.getMargin();
		float totalWidthForGraph = table.getPageSize().getWidth() - (2 * table.getMargin());
		float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getTopMargin() : table.getPageSize().getHeight() - table.getTopMargin();
		PDPage page = generatePage(doc);
		PDPageContentStream contentStream = generateContentStream(doc, page);
		addHeader(contentStream);
		URL resource = getClass().getClassLoader().getResource("images/graph1.jpg");
		assert resource != null;
		PDImageXObject pdImage = PDImageXObject.createFromFile(resource.getPath(), doc);
		float height = getGraphAdjustmentScale(totalHeightForGraph, pdImage.getHeight());
		float width = getGraphAdjustmentScale(totalWidthForGraph, pdImage.getWidth());
		tableTopY -= height;
		contentStream.drawImage(pdImage, table.getPageSize().getLowerLeftX() + table.getMargin(), tableTopY, width, height);
		contentStream.close();
	}

	private float getGraphAdjustmentScale(float total, int offset) {
		float width;
		if (offset > total) {
			width = total;
		} else {
			width = offset;
		}
		return width;
	}

	private float addHeader(PDPageContentStream contentStream) throws IOException {
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
			/*int textWidth = getTextWidth(table.getContentTextFont(), text, table.getContentFontSize());
			float adjustX = getAdjustX(table.getColumns().get(from).getAlignment(), table.getColumns().get(from).getWidth(), table.getCellPadding(),textWidth);*/
			nextY = drawFirstTwoHeaderLines(contentStream, label[i], nextY);
		}

		Set<Map.Entry<String, Object>> entries = headerMap.entrySet();
		int flag = 0;
		for (Map.Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			Object value = entry.getValue();

			writeText(contentStream, table.getContentTextColor(), table.getContentTextFont(), table.getContentFontSize(), nextY, xPos, key);

			xPos += cellWidth;

			writeText(contentStream, table.getContentTextColor(), table.getContentTextFont(), table.getContentFontSize(), nextY, xPos, value.toString());

			xPos += cellWidth;

			if ((flag % 2) != 0) {
				nextY -= table.getRowHeight();
				xPos = table.getMargin();
			}
			flag++;
		}


		return nextY;
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

	private float drawFirstTwoHeaderLines(PDPageContentStream contentStream, String label, float nextY) throws IOException {
		contentStream.beginText();
		contentStream.setFont(PDType1Font.TIMES_ROMAN, 8);
		int centerX = (int) table.getPageSize().getWidth() / 2 - label.length() / 2;
		contentStream.newLineAtOffset(centerX, nextY);
		contentStream.showText(label);
		contentStream.endText();
		nextY -= table.getRowHeight();
		return nextY;
	}

	private void addFooter(PDDocument doc) throws IOException {
		int numberOfPages = doc.getNumberOfPages();
		for (int pageNumber = 0; pageNumber < numberOfPages; pageNumber++) {
			PDPage page = doc.getPage(pageNumber);
			PDPageContentStream footerContentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
			drawHorizontalLine(doc, footerContentStream);
			drawLeftSection(doc, footerContentStream);
			drawRightSection(numberOfPages, pageNumber + 1, footerContentStream);
			footerContentStream.close();
		}
	}

	private void drawHorizontalLine(PDDocument doc, PDPageContentStream footerContentStream) throws IOException {
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

	private void drawLeftSection(PDDocument doc, PDPageContentStream footerContentStream) throws IOException {
		//URL resource = getClass().getClassLoader().getResource("images" + System.getProperty("file.separator") + "logo.png");
		URL resource = getClass().getClassLoader().getResource("images/logo.png");
		assert resource != null;
		PDImageXObject pdImage = PDImageXObject.createFromFile(resource.getPath(), doc);
		footerContentStream.drawImage(pdImage, table.getPageSize().getLowerLeftX() + table.getMargin(), table.getPageSize().getLowerLeftY() + 20, 30, 15);
	}

	private void drawRightSection(int numberOfPages, int pageNumber, PDPageContentStream footerContentStream) throws IOException {
		footerContentStream.beginText();
		footerContentStream.setFont(PDType1Font.TIMES_ROMAN, 8);
		footerContentStream.newLineAtOffset(table.getPageSize().getUpperRightX() - table.getMargin() - 40, table.getPageSize().getLowerLeftY() + 20);
		footerContentStream.showText("Page " + pageNumber + " of " + numberOfPages);
		footerContentStream.endText();
	}

	// Configures basic setup for the table and draws it page by page
	private void drawTable(PDDocument doc) throws IOException {
		// Calculate pagination
		Integer rowsPerPage;
		int numberOfPages;
		float pageWidth = table.getPageSize().getWidth();
		float totalRowWidth = table.getRowWidth();
		rowsPerPage = getRowsPerPage();
		List<List<Range>> rangesOfColumnRangePerPage;
		numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();
		List<Range> fixedColumns = table.getFixedColumns();
		boolean isFixedColumn = fixedColumns != null && fixedColumns.size() > 0;
		if (pageWidth < totalRowWidth) {
			rangesOfColumnRangePerPage = getRangesOfColumnRangePerPageNew(isFixedColumn);
		} else {
			rangesOfColumnRangePerPage = getSinglePageRange();
		}

		// Generate each page, get the content and draw it
		for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
			for (List<Range> range : rangesOfColumnRangePerPage) {
				PDPage page = generatePage(doc);
				PDPageContentStream contentStream = generateContentStream(doc, page);
				addHeader(contentStream);
				List<List<String>> currentPageContent = getContentForCurrentPage(rowsPerPage, pageCount, range);
				drawCurrentPage(currentPageContent, contentStream, range, isFixedColumn);
			}
		}
	}

	private List<List<Range>> getSinglePageRange() {
		List<List<Range>> lists = new ArrayList<>();
		List<Range> ranges = new ArrayList<>();
		float totalWidth = 0;
		Integer numberOfColumns = table.getNumberOfColumns();
		for (int i = 0; i < numberOfColumns; i++) {
			totalWidth += table.getColumns().get(i).getWidth();
		}
		Range range = getRange(numberOfColumns, totalWidth, 0);
		ranges.add(range);
		lists.add(ranges);
		return lists;
	}

	private List<List<Range>> getRangesOfColumnRangePerPageNew(boolean isFixedColumn) {
		List<List<Range>> listList = new ArrayList<>();
		float totalWidth = table.getPageSize().getWidth() - (table.getMargin() * 2);
		float xPos = totalWidth;

		int count = 0;
		float columnWidth = 0;
		float lastOffset;
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
				ranges = new ArrayList<>(fixedColumns);
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

		Range range = getRange(numberOfColumns, lastOffset, to + 1);
		ranges.add(range);
		listList.add(ranges);
	}

	private Range getRange(Integer numberOfColumns, float lastOffset, int i) {
		Range range = new Range();
		range.setFrom(i);
		range.setTo(numberOfColumns - 1);
		range.setOffSet(lastOffset);
		return range;
	}

	private float getOffset(List<Column> columns, int from, int to) {
		float fixedColumnWidth = 0;
		for (int i = from; i < to + 1; i++) {
			fixedColumnWidth += columns.get(i).getWidth();
		}
		return fixedColumnWidth;
	}

	private Integer getRowsPerPage() {
		return new Double(Math.floor((table.getHeight() - table.getColumnHeight()) / table.getRowHeight())).intValue();
	}

	// Draws current page table grid and border lines and content
	private void drawCurrentPage(List<List<String>> currentPageContent, PDPageContentStream contentStream, List<Range> ranges, boolean isFixedColumn)
			throws IOException {
		float nextX = 0;
		float nextTextXForNonZeroIndex = 0;
		for (Range range : ranges) {
			float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getTopMargin() : table.getPageSize().getHeight() - table.getTopMargin();

			// Draws grid and borders
			nextX = drawTableGrid(table, currentPageContent, contentStream, tableTopY, range, nextX, isFixedColumn);

			// Position cursor to start drawing content
			float nextTextX = table.getMargin() + table.getCellPadding();
			nextTextX = getNextX(range, nextTextXForNonZeroIndex, isFixedColumn, nextTextX);

			// Calculate center alignment for text in cell considering font height
			float nextTextY = tableTopY - (table.getRowHeight() / 2)
					- ((table.getContentTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table.getContentFontSize()) / 4);

			// Write column headers
			String[] columnsNamesAsArray = table.getColumnsNamesAsArray(table.getColumns().subList(range.getFrom(), range.getTo() + 1));
			contentStream.setNonStrokingColor(Color.white);
			writeHeaderContentLine(columnsNamesAsArray, contentStream, nextTextX, nextTextY, range);
			contentStream.setNonStrokingColor(Color.BLACK);
			nextTextY -= table.getColumnHeight();
			nextTextX = table.getMargin() + table.getCellPadding();
			nextTextX = getNextX(range, nextTextXForNonZeroIndex, isFixedColumn, nextTextX);

			// Write content
			for (List<String> strings : currentPageContent) {
				Object[] objects = strings.toArray();
				writeContentLine((String[]) objects, contentStream, nextTextX, nextTextY, table, range, ranges.get(0).getTo(), isFixedColumn);
				nextTextY -= table.getRowHeight();
				if (range.getFrom() == 0) {
					nextTextX = table.getMargin() + table.getCellPadding();
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
		int end;
		boolean startWithZeroIndex = from == 0;
		// start loop and end loop assignment.
		if (startWithZeroIndex) {
			i = 0;
			end = range.getTo() + 1;
		} else {
			if (!isFixedColumn) {
				i = 0;
				end = lineContent.length;
			} else {
				i = to + 1;
				end = lineContent.length;
			}
		}

		for (; i < end; i++) {
			String text = lineContent[i];
			int textWidth = getTextWidth(table.getContentTextFont(), text, table.getContentFontSize());
			float adjustX = getAdjustX(table.getColumns().get(from).getAlignment(), table.getColumns().get(from).getWidth(), table.getCellPadding(), textWidth);
			writeText(contentStream, table.getContentTextColor(), table.getContentTextFont(), table.getContentFontSize(), nextTextY, nextTextX + adjustX, text != null ? text : "");
			nextTextX += table.getColumns().get(from++).getWidth();
		}
	}


	// Writes the content for one line
	private void writeHeaderContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
										Range range) throws IOException {
		int from = range.getFrom();
		for (String text : lineContent) {
			int textWidth = getTextWidth(table.getHeaderTextFont(), text, table.getHeaderFontSize());
			float adjustX;
			if (textWidth > table.getColumns().get(from).getWidth()) {
				int characterPerLine = (int) ((table.getColumns().get(from).getWidth() - 4) * text.length()) / textWidth;
				String[] columnName = WordUtils.wrap(text, characterPerLine).split("\\r?\\n");
				for (int j = 0; j < columnName.length; j++) {
					textWidth = getTextWidth(table.getHeaderTextFont(), columnName[j] != null ? columnName[j] : "", table.getHeaderFontSize());
					adjustX = getAdjustX(table.getColumns().get(from).getAlignment(), table.getColumns().get(from).getWidth(), table.getCellPadding(), textWidth);
					writeText(contentStream, table.getHeaderTextColor(), table.getHeaderTextFont(), table.getHeaderFontSize(), nextTextY - j * table.getRowHeight(), nextTextX + adjustX, columnName[j] != null ? columnName[j] : "");
				}
			} else {
				adjustX = getAdjustX(table.getColumns().get(from).getAlignment(), table.getColumns().get(from).getWidth(), table.getCellPadding(), textWidth);
				writeText(contentStream, table.getHeaderTextColor(), table.getHeaderTextFont(), table.getHeaderFontSize(), nextTextY, nextTextX + adjustX, text != null ? text : "");
			}
			nextTextX += table.getColumns().get(from++).getWidth();
		}
	}


	private float drawTableGrid(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, float tableTopY, Range range, float expNextX, boolean isFixedColumn)
			throws IOException {
		float nextY = tableTopY;
		float nextX = table.getMargin();
		nextX = getNextX(range, expNextX, isFixedColumn, nextX);
		for (int column = range.getFrom(); column <= range.getTo(); column++) {
			float width = table.getColumns().get(column).getWidth();
			drawCellBackground(contentStream, nextX, nextY - table.getColumnHeight(), width, table.getColumnHeight(), table.getHeaderBackgroundColor());
			nextX += width;
		}

		//Reset X
		nextX = table.getMargin();

		//manage table header height
		for (int i = 0; i < 1; i++) {
			drawLine(contentStream, table.getLineColor(), table.getLineWidth(), table.getMargin(), table.getMargin() + range.getOffSet(), nextY, nextY);
			nextY -= table.getColumnHeight();
		}

		// Draw row lines
		for (int i = 0; i <= currentPageContent.size(); i++) {
			drawLine(contentStream, table.getLineColor(), table.getLineWidth(), table.getMargin(), table.getMargin() + range.getOffSet(), nextY, nextY);
			nextY -= table.getRowHeight();
		}

		// Draw column lines
		final float tableYLength = table.getColumnHeight() + (table.getRowHeight() * currentPageContent.size());
		final float tableBottomY = tableTopY - tableYLength;

		nextX = getNextX(range, expNextX, isFixedColumn, nextX);
		for (int i = range.getFrom(); i < range.getTo() + 1; i++) {
			drawLine(contentStream, table.getLineColor(), table.getLineWidth(), nextX, nextX, tableTopY, tableBottomY);
			nextX += table.getColumns().get(i).getWidth();
		}
		drawLine(contentStream, table.getLineColor(), table.getLineWidth(), nextX, nextX, tableTopY, tableBottomY);
		return nextX;
	}

	private float getNextX(Range range, float expNextX, boolean isFixedColumn, float nextX) {
		if (range.getFrom() != 0 && isFixedColumn) {
			nextX = expNextX;
		}
		return nextX;
	}


	private List<List<String>> getContentForCurrentPage(Integer rowsPerPage, int pageCount, List<Range> ranges) {
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

	private PDPage generatePage(PDDocument doc) {
		PDPage page = new PDPage();
		page.setMediaBox(table.getPageSize());
		page.setRotation(table.isLandscape() ? 90 : 0);
		doc.addPage(page);
		return page;
	}

	private PDPageContentStream generateContentStream(PDDocument doc, PDPage page) throws IOException {
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false);
		if (table.isLandscape()) {
			Matrix matrix = new Matrix(0, 1, -1, 0, table.getPageSize().getWidth(), 0);
			contentStream.transform(matrix);
		}
		//contentStream.setFont(table.getContentTextFont(), table.getContentFontSize());
		return contentStream;
	}


	private void drawLine(PDPageContentStream contentStream, Color lineColor, float lineWidth, float startX, float endX, float startY, float endY) throws IOException {
		contentStream.setStrokingColor(lineColor);
		contentStream.setLineWidth(lineWidth);
		contentStream.moveTo(startX, startY);
		contentStream.lineTo(endX, endY);
		contentStream.stroke();
		// Reset NonStrokingColor to default value
		contentStream.setStrokingColor(Color.BLACK);
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

	private void writeText(PDPageContentStream contentStream, Color textColor, PDFont textFont, float fontSize, float y, float x, String text) throws IOException {
		contentStream.beginText();
		contentStream.setNonStrokingColor(textColor);
		contentStream.setFont(textFont, fontSize);
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(text);
		contentStream.endText();
	}

	private float getAdjustX(Alignment textAlignment, float cellWidth, float cellMargin, int textWidth) {
		Alignment alignment = textAlignment == null ?
				Alignment.LEFT : textAlignment;
		float adjustment = 0;
		switch (alignment) {
			case LEFT: {
				adjustment = cellMargin;
			}
			break;
			case CENTER: {
				adjustment = getCenterX(cellWidth, cellMargin, textWidth);
			}
			break;
			case RIGHT: {
				adjustment = getRightX(cellWidth, cellMargin, textWidth);
			}
			break;
		}
		return adjustment;
	}

	private float getCenterX(float cellWidth, float cellMargin, int textWidth) {
		return (cellWidth - cellMargin - textWidth) / 2;
	}

	private float getRightX(float cellWidth, float cellMargin, int textWidth) {
		return (cellWidth - (textWidth + cellMargin * 2));
	}

	private int getTextWidth(PDFont textFont, String text, float fontSize) throws IOException {
		return (int) ((textFont.getStringWidth(text) / 1000) * fontSize);
	}

}
