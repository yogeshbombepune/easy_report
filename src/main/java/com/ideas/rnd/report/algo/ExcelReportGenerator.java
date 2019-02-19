package com.ideas.rnd.report.algo;

import com.ideas.rnd.report.model.excel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExcelReportGenerator implements ReportGenerator {
	private Workbook workbook;
	private Sheet sheet;
	private List<List<Cell>> columns;
	private List<List<Cell>> dataSet;
	private int rowNumber = 0;

	private ExcelReportGenerator() {
	}

	public ExcelReportGenerator(Workbook workbook, List<List<Cell>> columns, List<List<Cell>> dataSet) {
		this.columns = columns;
		this.dataSet = dataSet;
		this.workbook = workbook;
	}

	@Override
	public void generate() {
		createSheet();
		writeDataOnSheet();
	}

	private void createSheet() {
		Sheet sheet = this.workbook.createSheet();
		this.sheet = sheet;
	}

	private void writeDataOnSheet() {
		writeHeader();
		writeData();
	}

	private void writeData() {
		for (List<Cell> cells : this.dataSet) {
			Row headerRow = createRowObject();
			int cellNumber = 0;
			writeRow(cells, headerRow, cellNumber);
		}
	}

	private void writeRow(List<Cell> cells, Row headerRow, int cellNumber) {
		for (Cell cell : cells) {
			org.apache.poi.ss.usermodel.Cell headerCell = headerRow.createCell(cellNumber++);
			setValue(headerCell, cell.getValue());
			setStyle(headerCell, cell);
		}
	}

	private Row createRowObject() {
		return this.sheet.createRow(this.rowNumber++);
	}

	private void writeHeader() {
		for (List<Cell> cells : this.columns) {
			Row headerRow = createRowObject();
			int cellNumber = 0;
			writeRow(cells, headerRow, cellNumber);
		}
	}

	private void setStyle(org.apache.poi.ss.usermodel.Cell headerCell, Cell cell) {
		headerCell.setCellStyle(getCellStyle(cell));
	}

	private CellStyle getCellStyle(Cell cell) {
		CellStyle cellStyle = getCellStyle();
		setCellStyle(cell, cellStyle);
		return cellStyle;
	}


	/**
	 * @param cell
	 * @return
	 */
	private HorizontalAlignment getAlignment(Cell cell) {
		HorizontalAlignment alignment = null;
		switch (cell.getCellStyle().getAlignment()) {
			case CENTER: {
				alignment = HorizontalAlignment.CENTER;
			}
			break;
			case RIGHT: {
				alignment = HorizontalAlignment.RIGHT;
			}
			break;
			case LEFT: {
				alignment = HorizontalAlignment.LEFT;
			}
			break;
		}
		return alignment;
	}

	/**
	 * @return
	 */
	private CellStyle getCellStyle() {
		return this.workbook.createCellStyle();
	}

	/**
	 * @param cell
	 * @return
	 */
	private Font getFont(Cell cell) {
		Font headerFont = this.workbook.createFont();
		if (null != cell.getFont() && null != cell.getFont().getBold()) {
			headerFont.setBold(cell.getFont().getBold());
		}
		if (null != cell.getFont() && null != cell.getFont().getFontHeight()) {
			headerFont.setFontHeightInPoints(cell.getFont().getFontHeight());
		}
		if (null != cell.getFont() && null != cell.getFont().getColor()) {
			headerFont.setColor(cell.getFont().getColor());
		}
		return headerFont;
	}

	/**
	 * @param cell
	 * @param cellStyle
	 */
	private void setCellStyle(Cell cell, CellStyle cellStyle) {
		Font font = getFont(cell);
		cellStyle.setFont(font);

		if (null != cell.getCellStyle() && null != cell.getCellStyle().getWrapText()) {
			cellStyle.setWrapText(cell.getCellStyle().getWrapText());
		}

		if (null != cell.getCellStyle()) {
			HorizontalAlignment alignment = getAlignment(cell);
			if (null != alignment) {
				cellStyle.setAlignment(alignment);
			}
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderBottom()) {
			cellStyle.setBorderBottom(cell.getCellStyle().getBorderBottom());
		}

		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderRight()) {
			cellStyle.setBorderRight(cell.getCellStyle().getBorderRight());
		}

		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderTop()) {
			cellStyle.setBorderTop(cell.getCellStyle().getBorderTop());
		}

		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderLeft()) {
			cellStyle.setBorderLeft(cell.getCellStyle().getBorderLeft());
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getTopBorderColor()) {
			cellStyle.setTopBorderColor(cell.getCellStyle().getTopBorderColor());
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getLeftBorderColor()) {
			cellStyle.setLeftBorderColor(cell.getCellStyle().getLeftBorderColor());
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getRightBorderColor()) {
			cellStyle.setRightBorderColor(cell.getCellStyle().getRightBorderColor());
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBottomBorderColor()) {
			cellStyle.setBottomBorderColor(cell.getCellStyle().getBottomBorderColor());
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getFillPatternType()) {
			cellStyle.setFillPattern(cell.getCellStyle().getFillPatternType());
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getFillForegroundColor()) {
			cellStyle.setFillForegroundColor(cell.getCellStyle().getFillForegroundColor());
		}

		if (null != cell.getSpan()) {
			this.sheet.addMergedRegion(new CellRangeAddress(cell.getSpan().getFirstRow(), cell.getSpan().getLastRow(), cell.getSpan().getFirstCol(), cell.getSpan().getLastCol()));
		}

	}


	/**
	 * @param cell
	 * @param value
	 */
	private void setValue(org.apache.poi.ss.usermodel.Cell cell, Object value) {
		if (value instanceof String) {
			cell.setCellValue((String) value);
		} else if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof Calendar) {
			cell.setCellValue((Calendar) value);
		} else if (value instanceof RichTextString) {
			cell.setCellValue((RichTextString) value);
		}
	}


}
