package com.cluster.report.report.model.pdf;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
public class Table {
	// Table attributes
	private float margin;
	private float height;
	private PDRectangle pageSize;
	private boolean isLandscape;
	private float rowHeight;
	private float columnHeight;
	private boolean columnWordWrapEnable;
	private boolean columnSplitEnable;
	private String splitRegex;
	private float lineWidth;
	private Color lineColor;

	// font attributes
	private PDFont contentTextFont;
	private PDFont headerTextFont;

	private float contentFontSize;
	private float headerFontSize;
	private Color headerTextColor;
	private Color contentTextColor;
	private Color headerBackgroundColor;

	// Content attributes
	private Integer numberOfRows;
	private List<Column> columns;
	private List<Range> fixedColumns;
	private List<List<String>> content;
	private float cellPadding;

	public Integer getNumberOfColumns() {
		return null != this.getColumns() ? this.getColumns().size() : 0;
	}

	public String[] getColumnsNamesAsArray(List<Column> expColumns) {
		String[] columnNames = new String[expColumns.size()];
		for (int i = 0; i < expColumns.size(); i++) {
			columnNames[i] = expColumns.get(i).getName();
		}
		return columnNames;
	}
}
