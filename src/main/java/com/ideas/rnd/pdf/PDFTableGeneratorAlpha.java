package com.ideas.rnd.pdf;

import com.ideas.rnd.pdf.model.Range;
import com.ideas.rnd.pdf.model.Table;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PDFTableGeneratorAlpha {
	// Generates document from Table object
	public void generatePDF(Table table) throws IOException, COSVisitorException {
		PDDocument doc = null;
		try {
			doc = new PDDocument();
			drawTable(doc, table);
			doc.save("sample.pdf");
		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	// Configures basic setup for the table and draws it page by page
	public void drawTable(PDDocument doc, Table table) throws IOException {
		// Calculate pagination
		Integer rowsPerPage = null;
		Integer numberOfPages = null;
		float pageWidth = table.getPageSize().getWidth();
		float rowWidth = table.getRowWidth();
		rowsPerPage = getRowsPerPage(table);
		List<Range> rangesOfColumnRangePerPage = null;
		numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();
		if (pageWidth < rowWidth) {
			rangesOfColumnRangePerPage = getRangesOfColumnRangePerPage(table);
		} else {
			rangesOfColumnRangePerPage = getSinglePageRange(table);
		}

		// Generate each page, get the content and draw it
		for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
			for (Range range : rangesOfColumnRangePerPage) {
				PDPage page = generatePage(doc, table);
				PDPageContentStream contentStream = generateContentStream(doc, page, table);
				List<List<String>> currentPageContent = getContentForCurrentPage(table, rowsPerPage, pageCount, range);
				drawCurrentPage(table, currentPageContent, contentStream, range);
			}
		}
	}

	private List<Range> getSinglePageRange(Table table) {
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
		return ranges;
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
			if (xPos < 0) {
				int start = i - count;
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
	private void drawCurrentPage(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, Range range)
			throws IOException {
		float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getMargin() : table.getPageSize().getHeight() - table.getMargin();

		// Draws grid and borders
		drawTableGrid(table, currentPageContent, contentStream, tableTopY, range);

		// Position cursor to start drawing content
		float nextTextX = table.getMargin() + table.getCellMargin();
		// Calculate center alignment for text in cell considering font height
		float nextTextY = tableTopY - (table.getRowHeight() / 2)
				- ((table.getTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table.getFontSize()) / 4);

		// Write column headers
		String[] columnsNamesAsArray = table.getColumnsNamesAsArray(table.getColumns().subList(range.getFrom(), range.getTo() + 1));
		writeContentLine(columnsNamesAsArray, contentStream, nextTextX, nextTextY, table, range);
		nextTextY -= table.getRowHeight();
		nextTextX = table.getMargin() + table.getCellMargin();

		// Write content
		for (int i = 0; i < currentPageContent.size(); i++) {
			Object[] objects = currentPageContent.get(i).toArray();
			writeContentLine((String[]) objects, contentStream, nextTextX, nextTextY, table, range);
			nextTextY -= table.getRowHeight();
			nextTextX = table.getMargin() + table.getCellMargin();
		}

		contentStream.close();
	}

	// Writes the content for one line
	private void writeContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
								  Table table, Range range) throws IOException {
		int from = range.getFrom();
		for (int i = 0; i < lineContent.length; i++) {
			String text = lineContent[i];
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(nextTextX, nextTextY);
			contentStream.drawString(text != null ? text : "");
			contentStream.endText();
			nextTextX += table.getColumns().get(from++).getWidth();
		}
	}

	private void drawTableGrid(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, float tableTopY, Range range)
			throws IOException {
		// Draw row lines
		float nextY = tableTopY;
		for (int i = 0; i <= currentPageContent.size() + 1; i++) {
			contentStream.drawLine(table.getMargin(), nextY, table.getMargin() + range.getOffSet(), nextY);
			nextY -= table.getRowHeight();
		}

		// Draw column lines
		final float tableYLength = table.getRowHeight() + (table.getRowHeight() * currentPageContent.size());
		final float tableBottomY = tableTopY - tableYLength;
		float nextX = table.getMargin();
		for (int i = range.getFrom(); i < range.getTo() + 1; i++) {
			contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
			nextX += table.getColumns().get(i).getWidth();
		}
		contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
	}

	private List<List<String>> getContentForCurrentPage(Table table, Integer rowsPerPage, int pageCount, Range range) {
		int startRange = pageCount * rowsPerPage;
		int endRange = (pageCount * rowsPerPage) + rowsPerPage;
		if (endRange > table.getNumberOfRows()) {
			endRange = table.getNumberOfRows();
		}
		String[][] content = table.getContent();
		List<List<String>> newContentList = new ArrayList<>();
		for (int i = startRange; i < endRange; i++) {
			String[] specificData = Arrays.copyOfRange(content[i], range.getFrom(), range.getTo() + 1);
			newContentList.add(Arrays.asList(specificData));
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
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, false, false);
		// User transformation matrix to change the reference when drawing.
		// This is necessary for the landscape position to draw correctly
		if (table.isLandscape()) {
			contentStream.concatenate2CTM(0, 1, -1, 0, table.getPageSize().getWidth(), 0);
		}
		contentStream.setFont(table.getTextFont(), table.getFontSize());
		return contentStream;
	}
}
