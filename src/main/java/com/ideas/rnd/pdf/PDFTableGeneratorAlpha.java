package com.ideas.rnd.pdf;

import com.google.common.collect.ObjectArrays;
import com.ideas.rnd.pdf.model.Column;
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
		float totalRowWidth = table.getRowWidth();
		rowsPerPage = getRowsPerPage(table);
		List<List<Range>> rangesOfColumnRangePerPage;
		numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();
		if (pageWidth < totalRowWidth) {
			rangesOfColumnRangePerPage = getRangesOfColumnRangePerPageNew(table);
		} else {
			rangesOfColumnRangePerPage = getSinglePageRange(table);
		}

		// Generate each page, get the content and draw it
		for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
			for (List<Range> range : rangesOfColumnRangePerPage) {
				PDPage page = generatePage(doc, table);
				PDPageContentStream contentStream = generateContentStream(doc, page, table);
				List<List<String>> currentPageContent = getContentForCurrentPage(table, rowsPerPage, pageCount, range);
				drawCurrentPage(table, currentPageContent, contentStream, range);
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

	private List<List<Range>> getRangesOfColumnRangePerPageNew(Table table) {
		List<List<Range>> listList = new ArrayList<>();
		float totalWidth = table.getPageSize().getWidth();
		float xPos = totalWidth;

		int count = 0;
		float columnWidth = 0;
		float lastOffset = 0;
		List<Range> fixedColumns = table.getFixedColumns();
		float fixedColumnWidth = getOffset(table.getColumns(), fixedColumns.get(0).getFrom(), fixedColumns.get(0).getTo());
		fixedColumns.get(0).setOffSet(fixedColumnWidth);
		List<Range> ranges = null;
		for (int i = 0; i < table.getNumberOfColumns(); i++) {

			int start = i - count;
			float width = table.getColumns().get(i).getWidth();

			if (start != 0 && count == 0) {
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
		getLastOffsetNew(listList, fixedColumns, table.getNumberOfColumns(), columnWidth);
		return listList;

	}

	private void getLastOffsetNew(List<List<Range>> listList, List<Range> fixedColumns, Integer numberOfColumns, float lastOffset) {
		List<Range> ranges1 = listList.get(listList.size() - 1);
		int to = ranges1.get(ranges1.size() - 1).getTo();
		List<Range> ranges = new ArrayList<>();
		ranges.addAll(fixedColumns);
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
	private void drawCurrentPage(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, List<Range> ranges)
			throws IOException {
		float nextX = 0;
		float nextTextX = 0;
		for (Range range : ranges) {
			float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getMargin() : table.getPageSize().getHeight() - table.getMargin();

			// Draws grid and borders
			nextX = drawTableGrid(table, currentPageContent, contentStream, tableTopY, range, nextX);

			// Position cursor to start drawing content

			if (range.getFrom() == 0) {
				nextTextX = table.getMargin() + table.getCellMargin();
			}else {

			}

			// Calculate center alignment for text in cell considering font height
			float nextTextY = tableTopY - (table.getRowHeight() / 2)
					- ((table.getTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table.getFontSize()) / 4);

			// Write column headers
			String[] columnsNamesAsArray = table.getColumnsNamesAsArray(table.getColumns().subList(range.getFrom(), range.getTo() + 1));
			writeHeaderContentLine(columnsNamesAsArray, contentStream, nextTextX, nextTextY, table, range);
			nextTextY -= table.getRowHeight();
			if (range.getFrom() == 0) {
				nextTextX = table.getMargin() + table.getCellMargin();
			}


			// Write content
			for (int i = 0; i < currentPageContent.size(); i++) {
				Object[] objects = currentPageContent.get(i).toArray();
				writeContentLine((String[]) objects, contentStream, nextTextX, nextTextY, table, range);
				nextTextY -= table.getRowHeight();
				nextTextX = table.getMargin() + table.getCellMargin();
			}
		}


		contentStream.close();
	}

	// Writes the content for one line
	private void writeContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
								  Table table, Range range) throws IOException {
		int from = range.getFrom();
		int i;
		int end = 0;
		if (from == 0) {
			i = 0;
			end = range.getTo() + 1;
		} else {
			i = from;
			end = lineContent.length;
		}

		for (; i < end; i++) {
			String text = lineContent[i];
			contentStream.beginText();
			contentStream.moveTextPositionByAmount(nextTextX, nextTextY);
			contentStream.drawString(text != null ? text : "");
			contentStream.endText();
			nextTextX += table.getColumns().get(from++).getWidth();
		}
	}

	// Writes the content for one line
	private void writeHeaderContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
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

	private float drawTableGrid(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, float tableTopY, Range range, float expNextX)
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
		if (range.getFrom() != 0) {
			nextX = expNextX;
		}
		for (int i = range.getFrom(); i < range.getTo() + 1; i++) {
			contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
			nextX += table.getColumns().get(i).getWidth();
		}
		contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
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
