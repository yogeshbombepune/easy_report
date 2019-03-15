package com.ideas.rnd.report.algo;

import com.ideas.rnd.report.model.pdf.*;
import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Yogesh Bombe
 */
public class PdfReportGenerator implements ReportGenerator {
	private PDDocument doc;
	private Table table;
	private Header header;
	private Graph graph;
	private Footer footer;
	private float tableHeight;
	private float tableWidth;
	private int currentPageNumber = 0;
	private int totalNumberOfPages;
	private PDFont font;
	private int wrapLineAdjustment;

	/**
	 * Used for restrict to create object using No arg Constructor.
	 */
	@SuppressWarnings("unused")
	private PdfReportGenerator() {
	}

	/**
	 * @param doc
	 * @param header
	 * @param footer
	 * @param table
	 */
	public PdfReportGenerator(PDDocument doc, Header header, Footer footer, Table table) throws IOException {
		this.doc = doc;
		this.table = table;
		this.header = header;
		this.footer = footer;
		this.tableWidth = table.isLandscape() ? table.getPageSize().getHeight() - (table.getMargin() * 2) : table.getPageSize().getWidth() - (table.getMargin() * 2);
		this.tableHeight = table.isLandscape() ? table.getPageSize().getWidth() - (table.getMargin()) - getHeaderHeight() : table.getPageSize().getHeight() - (table.getMargin()) - getHeaderHeight();
	}

	/**
	 * @param doc
	 * @param font
	 * @param header
	 * @param footer
	 * @param table
	 */
	public PdfReportGenerator(PDDocument doc, PDFont font, Header header, Footer footer, Table table) throws IOException {
		this.doc = doc;
		this.font = font;
		this.table = table;
		this.header = header;
		this.footer = footer;
		this.tableWidth = table.isLandscape() ? table.getPageSize().getHeight() - (table.getMargin() * 2) : table.getPageSize().getWidth() - (table.getMargin() * 2);
		this.tableHeight = table.isLandscape() ? table.getPageSize().getWidth() - (table.getMargin()) - getHeaderHeight() : table.getPageSize().getHeight() - (table.getMargin()) - getHeaderHeight();
	}

	/**
	 * @param doc
	 * @param font
	 * @param header
	 * @param footer
	 * @param table
	 * @param graph
	 */
	public PdfReportGenerator(PDDocument doc, PDFont font, Header header, Footer footer, Table table, Graph graph) throws IOException {
		this.doc = doc;
		this.header = header;
		this.footer = footer;
		this.font = font;
		this.table = table;
		this.graph = graph;
		this.tableWidth = table.isLandscape() ? table.getPageSize().getHeight() - (table.getMargin() * 2) : table.getPageSize().getWidth() - (table.getMargin() * 2);
		this.tableHeight = table.isLandscape() ? table.getPageSize().getWidth() - (table.getMargin()) - getHeaderHeight() : table.getPageSize().getHeight() - (table.getMargin()) - getHeaderHeight();
	}

	/**
	 * @param header
	 * @param footer
	 * @param table
	 * @param graph
	 */
	public PdfReportGenerator(PDDocument doc, Header header, Footer footer, Table table, Graph graph) throws IOException {
		this.doc = doc;
		this.header = header;
		this.footer = footer;
		this.table = table;
		this.graph = graph;
		this.tableWidth = table.isLandscape() ? table.getPageSize().getHeight() - (table.getMargin() * 2) : table.getPageSize().getWidth() - (table.getMargin() * 2);
		this.tableHeight = table.isLandscape() ? table.getPageSize().getWidth() - (table.getMargin()) - getHeaderHeight() : table.getPageSize().getHeight() - (table.getMargin()) - getHeaderHeight();
	}

	/**
	 * Generates document from initialize data objects.
	 *
	 * @throws IOException
	 */
	public void generate() throws IOException {
		drawTable();
		addGraph();
	}

	private void addGraph() throws IOException {
		if (this.graph != null && this.graph.getGraphs() != null && this.graph.getGraphs().size() > 0) {
			addGraphs();
		}
	}

	/**
	 * @return calculated height required for header section.
	 */
	private float getHeaderHeight() throws IOException {
		evaluateHeaderHeight();
		return (float) (this.wrapLineAdjustment + 1.5) * this.table.getRowHeight();
	}

	private void evaluateHeaderHeight() throws IOException {
		int flag = 0;
		float numberOfLinesRequire = 0;
		for (Map.Entry<String, Object> entry : this.header.getMetaKeyVal().entrySet()) {
			Object value = entry.getValue();
			int textWidth = getTextWidth(header.getMetaKeyValFont(), null != value ? value.toString() : "", header.getMetaKeyValFontSize());
			float cellWidth = this.tableWidth / 4;
			int numberOfColumns;
			if (flag % 2 == 0) {
				numberOfColumns = 3;
			} else {
				numberOfColumns = 1;
			}
			if (textWidth > (cellWidth * numberOfColumns)) {
				int characterPerLine = (int) ((cellWidth * numberOfColumns) * entry.getValue().toString().length()) / textWidth;
				String[] columnName = WordUtils.wrap(entry.getValue().toString(), characterPerLine).split("\\r?\\n");
				numberOfLinesRequire += columnName.length;
			} else {
				numberOfLinesRequire += 0.50f;
			}
			flag++;
		}
		this.wrapLineAdjustment = (int) Math.ceil(numberOfLinesRequire);

	}

	/**
	 * Used to draw graphs
	 *
	 * @throws IOException
	 */
	private void addGraphs() throws IOException {
		for (File graphFile : this.graph.getGraphs()) {
			if (graphFile.exists()) {
				drawGraph(this.tableHeight, this.tableWidth, graphFile);
			}
		}
	}

	/**
	 * @param totalHeightForGraph
	 * @param totalWidthForGraph
	 * @param graphFile
	 * @throws IOException
	 */
	private void drawGraph(final float totalHeightForGraph, final float totalWidthForGraph, File graphFile) throws IOException {
		PDPage page = generatePage();
		PDPageContentStream contentStream = generateContentStream(page);
		addHeader(contentStream);
		PDImageXObject pdImage = PDImageXObject.createFromFileByExtension(graphFile, doc);
		int adjustment = 10;
		float height = getGraphAdjustmentScale(totalHeightForGraph - table.getMargin() - adjustment, pdImage.getHeight());
		float width = getGraphAdjustmentScale(totalWidthForGraph, pdImage.getWidth());
		float x = (totalWidthForGraph / 2) - (width / 2) + this.table.getMargin();
		float y = ((totalHeightForGraph + table.getMargin() + adjustment) / 2) - (height / 2);
		contentStream.drawImage(pdImage, x, y, width, height);
		printFooter(doc, contentStream);
		contentStream.close();
	}

	/**
	 * @param total
	 * @param offset
	 * @return adjusted height and width of graph.
	 */
	private float getGraphAdjustmentScale(float total, int offset) {
		float width;
		if (offset > total) {
			width = total;
		} else {
			width = offset;
		}
		return width;
	}

	/**
	 * @param contentStream
	 * @return
	 * @throws IOException
	 */
	private void addHeader(PDPageContentStream contentStream) throws IOException {
		float xPos = table.getMargin();
		float totalWidth = this.tableWidth;
		float cellWidth = totalWidth / 4;
		float pageTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getMargin() : table.getPageSize().getHeight() - table.getMargin();
		float heightForHeaderBackground = (this.wrapLineAdjustment - 1) * table.getRowHeight();
		int fixedLine = 2;
		drawCellBackground(contentStream, table.getMargin(), pageTopY - 3 - (table.getRowHeight() * (fixedLine + this.wrapLineAdjustment - 1)), totalWidth, heightForHeaderBackground, Color.LIGHT_GRAY);
		float nextY = pageTopY;
		writeHeadingOne(contentStream, nextY);
		nextY -= table.getRowHeight();
		writeHeadingTwo(contentStream, nextY);
		nextY -= table.getRowHeight();
		writeHeaderLabels(contentStream, xPos, cellWidth, nextY);
	}

	/**
	 * @param contentStream
	 * @param xPos
	 * @param cellWidth
	 * @param nextY
	 * @throws IOException
	 */
	private void writeHeaderLabels(PDPageContentStream contentStream, float xPos, float cellWidth, float nextY) throws IOException {
		Map<String, Object> headerMap = header.getMetaKeyVal();
		Set<Map.Entry<String, Object>> entries = headerMap.entrySet();
		int flag = 0;
		for (Map.Entry<String, Object> entry : entries) {

			writeText(contentStream, header.getMetaKeyValColor(), header.getMetaKeyValFont(), header.getMetaKeyValFontSize(), nextY, xPos, entry.getKey());

			xPos += cellWidth;

			int textWidth = getTextWidth(header.getMetaKeyValFont(), null != entry.getValue() ? entry.getValue().toString() : "", header.getMetaKeyValFontSize());

			if (textWidth > cellWidth * 3) {
				int characterPerLine = (int) ((cellWidth * 3) * entry.getValue().toString().length()) / textWidth;
				String[] columnName = WordUtils.wrap(entry.getValue().toString(), characterPerLine).split("\\r?\\n");
				for (int j = 0; j < columnName.length; j++) {
					writeText(contentStream, header.getMetaKeyValColor(), header.getMetaKeyValFont(), header.getMetaKeyValFontSize(), nextY, xPos, columnName[j]);
					nextY -= table.getRowHeight();
				}
			} else {
				writeText(contentStream, header.getMetaKeyValColor(), header.getMetaKeyValFont(), header.getMetaKeyValFontSize(), nextY, xPos, null != entry.getValue() ? entry.getValue().toString() : "");
			}

			xPos += cellWidth;

			if ((flag % 2) != 0) {
				nextY -= table.getRowHeight();
				xPos = table.getMargin();
			}
			flag++;
		}
	}

	/**
	 * @param contentStream
	 * @param nextY
	 * @throws IOException
	 */
	private void writeHeadingTwo(PDPageContentStream contentStream, float nextY) throws IOException {
		float width = table.isLandscape() ? table.getPageSize().getHeight() : table.getPageSize().getWidth();
		int textWidth = getTextWidth(header.getReportNameFont(), header.getReportName(), header.getReportNameFontSize());
		float adjustX = getAdjustX(Alignment.CENTER, width, 0, textWidth);
		writeText(contentStream, header.getReportNameColor(), header.getReportNameFont(), header.getReportNameFontSize(), nextY, adjustX, header.getReportName());
	}

	/**
	 * @param contentStream
	 * @param nextY
	 * @throws IOException
	 */
	private void writeHeadingOne(PDPageContentStream contentStream, float nextY) throws IOException {
		float width = table.isLandscape() ? table.getPageSize().getHeight() : table.getPageSize().getWidth();
		int textWidth = getTextWidth(header.getPropertyNameFont(), header.getPropertyName(), header.getPropertyNameFontSize());
		float adjustX = getAdjustX(Alignment.CENTER, width, 0, textWidth);
		writeText(contentStream, header.getPropertyNameColor(), header.getPropertyNameFont(), header.getPropertyNameFontSize(), nextY, adjustX, header.getPropertyName());
	}

	private void printFooter(PDDocument doc, PDPageContentStream footerContentStream) throws IOException {
		float endX = this.table.isLandscape() ? table.getPageSize().getHeight() : table.getPageSize().getUpperRightX();
		drawLine(footerContentStream, this.footer.getLineColor(), this.footer.getLineWidth(),
				table.getPageSize().getLowerLeftX() + table.getMargin(),
				endX - table.getMargin(),
				table.getPageSize().getLowerLeftY() + table.getMargin(),
				table.getPageSize().getLowerLeftY() + table.getMargin());
		drawLeftSection(doc, footerContentStream);
		drawRightSection(this.totalNumberOfPages, ++this.currentPageNumber, footerContentStream);
	}

	/**
	 * @param doc
	 * @param footerContentStream
	 * @throws IOException
	 */
	private void drawLeftSection(PDDocument doc, PDPageContentStream footerContentStream) throws IOException {
		if (null != this.footer && null != this.footer.getLogoImage()) {
			PDImageXObject pdImage = PDImageXObject.createFromFileByExtension(this.footer.getLogoImage(), doc);
			footerContentStream.drawImage(pdImage, table.getPageSize().getLowerLeftX() + table.getMargin(),
					table.getPageSize().getLowerLeftY() + 20, 35, 15);
		}
	}

	/**
	 * @param numberOfPages
	 * @param pageNumber
	 * @param footerContentStream
	 * @throws IOException
	 */
	private void drawRightSection(int numberOfPages, int pageNumber, PDPageContentStream footerContentStream) throws IOException {
		float x = this.table.isLandscape() ? table.getPageSize().getHeight() - table.getMargin() - 40 : table.getPageSize().getUpperRightX() - table.getMargin() - 40;
		writeText(footerContentStream,
				this.footer.getPageNumberPhraseColor(), this.footer.getPageNumberPhraseFont(),
				this.footer.getPageNumberPhraseSize(),
				table.getPageSize().getLowerLeftY() + 25,
				x,
				String.format(this.footer.getPageNumberPhrase(), pageNumber, numberOfPages));
	}


	/**
	 * Configures basic setup for the table and draws it page by page
	 *
	 * @throws IOException
	 */
	private void drawTable() throws IOException {
		int numberOfPages;
		List<List<Range>> rangesOfColumnRangePerPage = null;
		// Check Fixed Column
		boolean isFixedColumn = isFixedColumn();
		// Calculate pagination
		Integer rowsPerPage = getRowsPerPage();
		if (null != this.table.getColumns() &&
				this.table.getColumns().size() > 0 &&
				null != table.getContent() &&
				table.getContent().size() > 0) {
			rangesOfColumnRangePerPage = getRanges(isFixedColumn);
			numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();
		} else {
			numberOfPages = 1;
		}
		calculatePageCountForFooter(numberOfPages, null != rangesOfColumnRangePerPage ? rangesOfColumnRangePerPage.size() : 1);
		renderPages(numberOfPages, rangesOfColumnRangePerPage, isFixedColumn, rowsPerPage);
	}

	private List<List<Range>> getRanges(boolean isFixedColumn) {
		List<List<Range>> rangesOfColumnRangePerPage;
		float totalRequiredWidth = (float) table.getColumns().stream().mapToDouble(Column::getWidth).sum();
		if (this.tableWidth < totalRequiredWidth) {
			rangesOfColumnRangePerPage = getRangesOfColumnRangePerPageNew(isFixedColumn);
		} else {
			rangesOfColumnRangePerPage = getSinglePageRange();
		}
		return rangesOfColumnRangePerPage;
	}

	private void renderPages(int numberOfPages, List<List<Range>> rangesOfColumnRangePerPage, boolean isFixedColumn, Integer rowsPerPage) throws IOException {
		// Generate each page, get the content and draw it
		for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
			if (null != this.table.getColumns() &&
					this.table.getColumns().size() > 0 &&
					null != rangesOfColumnRangePerPage &&
					rangesOfColumnRangePerPage.size() > 0) {
				renderTable(rangesOfColumnRangePerPage, isFixedColumn, rowsPerPage, pageCount);
			} else {
				generateBlankPageWithOnlyHeaderAndFooter();
			}
		}
	}

	private void renderTable(List<List<Range>> rangesOfColumnRangePerPage, boolean isFixedColumn, Integer rowsPerPage, int pageCount) throws IOException {
		for (List<Range> range : rangesOfColumnRangePerPage) {
			PDPage page = generatePage();
			PDPageContentStream contentStream = generateContentStream(page);
			addHeader(contentStream);
			List<List<String>> currentPageContent = getContentForCurrentPage(rowsPerPage, pageCount, range);
			drawCurrentPage(currentPageContent, contentStream, range, isFixedColumn);
			printFooter(doc, contentStream);
			contentStream.close();
		}
	}

	private void generateBlankPageWithOnlyHeaderAndFooter() throws IOException {
		PDPage page = generatePage();
		PDPageContentStream contentStream = generateContentStream(page);
		addHeader(contentStream);
		printFooter(doc, contentStream);
		contentStream.close();
	}

	private boolean isFixedColumn() {
		return table.getFixedColumns() != null && table.getFixedColumns().size() > 0;
	}


	private void calculatePageCountForFooter(int numberOfPages, int ranges) {
		this.totalNumberOfPages = numberOfPages * ranges;
		if (this.graph != null && this.graph.getGraphs() != null && this.graph.getGraphs().size() > 0) {
			this.totalNumberOfPages += this.graph.getGraphs().size();
		}
	}

	/**
	 * @return calculated table column configuration for column split.
	 */
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

	/**
	 * @param isFixedColumn
	 * @return
	 */
	private List<List<Range>> getRangesOfColumnRangePerPageNew(boolean isFixedColumn) {
		List<List<Range>> listList = new ArrayList<>();
		float totalWidth = this.tableWidth;
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

	/**
	 * @param listList
	 * @param fixedColumns
	 * @param isFixedColumn
	 * @param numberOfColumns
	 * @param lastOffset
	 */
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

	/**
	 * @param numberOfColumns
	 * @param lastOffset
	 * @param i
	 * @return
	 */
	private Range getRange(Integer numberOfColumns, float lastOffset, int i) {
		Range range = new Range();
		range.setFrom(i);
		range.setTo(numberOfColumns - 1);
		range.setOffSet(lastOffset);
		return range;
	}

	/**
	 * @param columns
	 * @param from
	 * @param to
	 * @return
	 */
	private float getOffset(List<Column> columns, int from, int to) {
		float fixedColumnWidth = 0;
		for (int i = from; i < to + 1; i++) {
			fixedColumnWidth += columns.get(i).getWidth();
		}
		return fixedColumnWidth;
	}

	/**
	 * @return number of rows per page.
	 */
	private Integer getRowsPerPage() {
		return new Double(Math.floor((this.tableHeight - this.table.getMargin() - table.getColumnHeight()) / table.getRowHeight())).intValue() - 1;
	}

	/**
	 * Draws current page table grid and border lines and content
	 */
	private void drawCurrentPage(List<List<String>> currentPageContent, PDPageContentStream contentStream, List<Range> ranges, boolean isFixedColumn)
			throws IOException {
		float nextX = 0;
		float nextTextXForNonZeroIndex = 0;
		for (Range range : ranges) {

			float tableTopY = this.tableHeight;

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
			writeHeaderContentLine(columnsNamesAsArray, contentStream, nextTextX, nextTextY, range);
			nextTextY -= table.getColumnHeight();
			nextTextX = table.getMargin() + table.getCellPadding();
			nextTextX = getNextX(range, nextTextXForNonZeroIndex, isFixedColumn, nextTextX);

			// Write content
			for (List<String> strings : currentPageContent) {
				Object[] objectArray = strings.toArray();
				String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
				writeContentLine(stringArray, contentStream, nextTextX, nextTextY, table, range, ranges.get(0).getTo(), isFixedColumn);
				nextTextY -= table.getRowHeight();
				if (range.getFrom() == 0) {
					nextTextX = table.getMargin() + table.getCellPadding();
				}
			}

			nextTextXForNonZeroIndex = nextTextX + ranges.get(0).getOffSet();
		}
	}

	/**
	 * Writes the content for one line
	 *
	 * @param lineContent
	 * @param contentStream
	 * @param nextTextX
	 * @param nextTextY
	 * @param table
	 * @param range
	 * @param to
	 * @param isFixedColumn
	 * @throws IOException
	 */
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
			float adjustX = getAdjustX(null != table.getColumns().get(from).getContentAlignment() ? table.getColumns().get(from).getContentAlignment() : table.getColumns().get(from).getHeaderAlignment(), table.getColumns().get(from).getWidth(), table.getCellPadding(), textWidth);
			writeText(contentStream, table.getContentTextColor(), table.getContentTextFont(), table.getContentFontSize(), nextTextY, nextTextX + adjustX, text != null ? text : "");
			nextTextX += table.getColumns().get(from++).getWidth();
		}
	}


	/**
	 * Writes the content for one line
	 */
	private void writeHeaderContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
										Range range) throws IOException {
		int from = range.getFrom();
		for (String text : lineContent) {
			int textWidth = getTextWidth(table.getHeaderTextFont(), text, table.getHeaderFontSize());
			float adjustX;
			if (textWidth > table.getColumns().get(from).getWidth()) {
				int characterPerLine = (int) ((table.getColumns().get(from).getWidth() - 4) * text.length()) / textWidth;
				String[] columnName;
				if (table.isColumnWordWrapEnable()) {
					columnName = WordUtils.wrap(text, characterPerLine).split("\\r?\\n");
				} else {
					if (null != table.getSplitRegex())
						columnName = text.split(Pattern.quote(table.getSplitRegex()));
					else
						columnName = text.split("\\r?\\n?,");
				}
				for (int j = 0; j < columnName.length; j++) {
					textWidth = getTextWidth(table.getHeaderTextFont(), columnName[j] != null ? columnName[j].trim() : "", table.getHeaderFontSize());
					adjustX = getAdjustX(table.getColumns().get(from).getHeaderAlignment(), table.getColumns().get(from).getWidth(), table.getCellPadding(), textWidth);
					writeText(contentStream, table.getHeaderTextColor(), table.getHeaderTextFont(), table.getHeaderFontSize(), nextTextY - j * table.getRowHeight(), nextTextX + adjustX, columnName[j] != null ? columnName[j].trim() : "");
				}
			} else {
				adjustX = getAdjustX(table.getColumns().get(from).getHeaderAlignment(), table.getColumns().get(from).getWidth(), table.getCellPadding(), textWidth);
				writeText(contentStream, table.getHeaderTextColor(), table.getHeaderTextFont(), table.getHeaderFontSize(), nextTextY, nextTextX + adjustX, text != null ? text : "");
			}
			nextTextX += table.getColumns().get(from++).getWidth();
		}
	}


	/**
	 * @param table
	 * @param currentPageContent
	 * @param contentStream
	 * @param tableTopY
	 * @param range
	 * @param expNextX
	 * @param isFixedColumn
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * @param range
	 * @param expNextX
	 * @param isFixedColumn
	 * @param nextX
	 * @return
	 */
	private float getNextX(Range range, float expNextX, boolean isFixedColumn, float nextX) {
		if (range.getFrom() != 0 && isFixedColumn) {
			nextX = expNextX;
		}
		return nextX;
	}


	/**
	 * @param rowsPerPage
	 * @param pageCount
	 * @param ranges
	 * @return
	 */
	private List<List<String>> getContentForCurrentPage(Integer rowsPerPage, int pageCount, List<Range> ranges) {
		List<List<String>> newContentList = new ArrayList<>();

		int startRange = pageCount * rowsPerPage;
		int endRange = (pageCount * rowsPerPage) + rowsPerPage;
		if (endRange > table.getNumberOfRows()) {
			endRange = table.getNumberOfRows();
		}
		List<List<String>> content = table.getContent();
		for (int i = startRange; i < endRange; i++) {
			List<String> commonDataChunk = new ArrayList<>(content.get(i).subList(ranges.get(0).getFrom(), ranges.get(0).getTo() + 1));
			if (ranges.size() > 1) {
				List<String> variableChunkOfData = new ArrayList<>(content.get(i).subList(ranges.get(1).getFrom(), ranges.get(1).getTo() + 1));
				commonDataChunk.addAll(variableChunkOfData);
			}
			newContentList.add(commonDataChunk);
		}

		return newContentList;
	}

	/**
	 * @return
	 */
	private PDPage generatePage() {
		PDPage page = new PDPage();
		page.setMediaBox(table.getPageSize());
		page.setRotation(table.isLandscape() ? 90 : 0);
		this.doc.addPage(page);
		return page;
	}

	/**
	 * @param page
	 * @return
	 * @throws IOException
	 */
	private PDPageContentStream generateContentStream(PDPage page) throws IOException {
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
		if (table.isLandscape()) {
			Matrix matrix = new Matrix(0, 1, -1, 0, table.getPageSize().getWidth(), 0);
			contentStream.transform(matrix);
		}
		return contentStream;
	}


	/**
	 * @param contentStream
	 * @param lineColor
	 * @param lineWidth
	 * @param startX
	 * @param endX
	 * @param startY
	 * @param endY
	 * @throws IOException
	 */
	private void drawLine(PDPageContentStream contentStream, Color lineColor, float lineWidth, float startX, float endX, float startY, float endY) throws IOException {
		contentStream.setStrokingColor(lineColor);
		contentStream.setLineWidth(lineWidth);
		contentStream.moveTo(startX, startY);
		contentStream.lineTo(endX, endY);
		contentStream.stroke();
		// Reset NonStrokingColor to default value
		contentStream.setStrokingColor(Color.BLACK);
	}

	/**
	 * @param contentStream
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 * @param color
	 * @throws IOException
	 */
	private void drawCellBackground(PDPageContentStream contentStream, final float startX, final float startY, final float width, final float height, Color color)
			throws IOException {
		contentStream.setNonStrokingColor(color);
		contentStream.addRect(startX, startY, width, height);
		contentStream.fill();
		contentStream.closePath();
		// Reset NonStrokingColor to default value
		contentStream.setNonStrokingColor(Color.BLACK);
	}

	/**
	 * @param contentStream
	 * @param textColor
	 * @param textFont
	 * @param fontSize
	 * @param y
	 * @param x
	 * @param text
	 * @throws IOException
	 */
	private void writeText(PDPageContentStream contentStream, Color textColor, PDFont textFont, float fontSize, float y, float x, String text) throws IOException {
		contentStream.beginText();
		contentStream.setNonStrokingColor(textColor);
		if (null != this.font) {
			contentStream.setFont(this.font, fontSize);
		} else {
			contentStream.setFont(textFont, fontSize);
		}
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(null != text ? text : "");
		contentStream.endText();
	}

	/**
	 * @param textAlignment
	 * @param cellWidth
	 * @param cellMargin
	 * @param textWidth
	 * @return
	 */
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

	/**
	 * @param cellWidth
	 * @param cellMargin
	 * @param textWidth
	 * @return
	 */
	private float getCenterX(float cellWidth, float cellMargin, int textWidth) {
		return (cellWidth - cellMargin - textWidth) / 2;
	}

	/**
	 * @param cellWidth
	 * @param cellMargin
	 * @param textWidth
	 * @return
	 */
	private float getRightX(float cellWidth, float cellMargin, int textWidth) {
		return (cellWidth - (textWidth + cellMargin * 2));
	}

	/**
	 * @param textFont
	 * @param text
	 * @param fontSize
	 * @return
	 * @throws IOException
	 */
	private int getTextWidth(PDFont textFont, String text, float fontSize) throws IOException {
		if (null != this.font) {
			return (int) ((this.font.getStringWidth(text) / 1000) * fontSize);
		} else {
			return (int) ((textFont.getStringWidth(text) / 1000) * fontSize);
		}

	}

}
