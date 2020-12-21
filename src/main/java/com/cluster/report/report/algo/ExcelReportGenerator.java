package com.cluster.report.report.algo;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;

import com.cluster.report.report.model.excel.Cell;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExcelReportGenerator implements ReportGenerator {
	private Workbook workbook;
	private Sheet sheet;
	private List<List<Cell>> columns;
	private List<List<Cell>> dataSet;
	private int rowNumber = 0;
	private String imagePath;

	private ExcelReportGenerator() {
	}

	public ExcelReportGenerator(Workbook workbook, List<List<Cell>> columns, List<List<Cell>> dataSet, String imagePath) {
		this.columns = columns;
		this.dataSet = dataSet;
		this.workbook = workbook;
		this.imagePath = imagePath;
	}

	@Override
	public void generate() {
		createSheet();
		writeDataOnSheet();
		addImage();
	}

	private void addImage() {
		InputStream feedChartToExcel = null;
		try {
			Sheet sheet2 = workbook.createSheet("Graph");
			//feedChartToExcel = new FileInputStream("C:\\Users\\idnyob\\Desktop\\PcrChartImageFogX7eRH4c1551955300676.png");
			feedChartToExcel = new FileInputStream(this.imagePath);

			// Convert picture to be added into a byte array
			byte[] bytes = IOUtils.toByteArray(feedChartToExcel);


			// Add Picture to Workbook, Specify picture type as PNG and Get an Index
			int pictureId = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			// Close the InputStream. We are ready to attach the image to workbook now
			feedChartToExcel.close();
			// Create the drawing container
			XSSFDrawing drawing = (XSSFDrawing) sheet2.createDrawingPatriarch();
			//  Create an anchor point
			XSSFClientAnchor anchor = new XSSFClientAnchor();
			//  Define top left corner, and we can resize picture suitable from there


			anchor.setDx1(25 * XSSFShape.EMU_PER_PIXEL);
			anchor.setDx2(41 * XSSFShape.EMU_PER_PIXEL);
			anchor.setDy1(1 * XSSFShape.EMU_PER_PIXEL);
			anchor.setDy2(14 * XSSFShape.EMU_PER_PIXEL);
			// Invoke createPicture and pass the anchor point and ID
			XSSFPicture picture = drawing.createPicture(anchor, pictureId);
			// Call resize method, which resizes the image


			picture.resize();

		} catch (Exception e) {
			e.printStackTrace();
		}
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
			//headerFont.setBold(cell.getFont().getBold());
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
				//cellStyle.setAlignment(alignment);
			}
		}
		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderBottom()) {
			//cellStyle.setBorderBottom(cell.getCellStyle().getBorderBottom());
		}

		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderRight()) {
			//cellStyle.setBorderRight(cell.getCellStyle().getBorderRight());
		}

		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderTop()) {
			//cellStyle.setBorderTop(cell.getCellStyle().getBorderTop());
		}

		if (null != cell.getCellStyle() && null != cell.getCellStyle().getBorderLeft()) {
			//cellStyle.setBorderLeft(cell.getCellStyle().getBorderLeft());
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
			//cellStyle.setFillPattern(cell.getCellStyle().getFillPatternType());
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
