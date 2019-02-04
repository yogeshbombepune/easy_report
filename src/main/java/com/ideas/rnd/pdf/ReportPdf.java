package com.ideas.rnd.pdf;

import com.ideas.rnd.pdf.model.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ReportPdf {
	/**
	 * Page Configuration
	 */
	PDRectangle PAGE_SIZE = PDRectangle.A4;
	float MARGIN = 36;
	boolean IS_LANDSCAPE = true;

	/**
	 * Page Header Configuration
	 */
	Color PAGE_HEADER_PROPERTY_NAME_COLOR = Color.BLACK;
	PDFont PAGE_HEADER_PROPERTY_NAME_FONT = PDType1Font.COURIER_BOLD;
	float PAGE_HEADER_PROPERTY_NAME_SIZE = 13;
	Color PAGE_HEADER_REPORT_NAME_COLOR = Color.BLACK;
	PDFont PAGE_HEADER_REPORT_NAME_FONT = PDType1Font.COURIER;
	float PAGE_HEADER_REPORT_NAME_FONT_SIZE = 11;
	Color PAGE_HEADER_META_KEY_VAL_COLOR = Color.BLACK;
	float PAGE_HEADER_META_KEY_VAL_FONT_SIZE = 8;
	PDFont PAGE_HEADER_META_KEY_VAL_FONT = PDType1Font.TIMES_ROMAN;


	/**
	 * Footer Configuration
	 */
	Color PAGE_FOOTER_LINE_COLOR = Color.lightGray;
	float PAGE_FOOTER_LINE_FONT_SIZE = 0.6f;
	Color PAGE_FOOTER_PAGE_NUMBER_PHRASE_COLOR = Color.BLACK;
	PDFont PAGE_FOOTER_PAGE_NUMBER_PHRASE_FONT = PDType1Font.TIMES_BOLD;
	float PAGE_FOOTER_PAGE_NUMBER_PHRASE_FONT_SIZE = 8;
	String PAGE_FOOTER_PAGE_NUMBER_PHRASE = "Page %s of %s";


	/**
	 * Table Header Configuration
	 */
	PDFont TABLE_HEADER_TEXT_FONT = PDType1Font.HELVETICA_BOLD_OBLIQUE;
	float TABLE_HEADER_FONT_SIZE = 8;
	Color TABLE_HEADER_TEXT_COLOR = Color.WHITE;
	Color TABLE_HEADER_BACKGROUND_COLOR = new Color(2, 43, 87);
	Color TABLE_LINE_COLOR = Color.LIGHT_GRAY;
	float TABLE_LINE_WIDTH = 0.5f;

	// Use when multi line table column formatting needs.
	boolean TABLE_HEADER_WORD_WRAP_ENABLE = true;
	boolean TABLE_HEADER_SPLIT_ENABLE = !TABLE_HEADER_WORD_WRAP_ENABLE;
	String TABLE_HEADER_SPLIT_REGEX_WITH_COMMA = ",";
	String TABLE_HEADER_SPLIT_REGEX_WITH_PIPE = "|";
	String TABLE_HEADER_SPLIT_REGEX_WITH_DOUBLE_PIPE = "||";

	/**
	 * Table Content Configuration
	 */
	PDFont TABLE_CONTENT_TEXT_FONT = PDType1Font.HELVETICA;
	float TABLE_CONTENT_FONT_SIZE = 8;
	Color TABLE_CONTENT_TEXT_COLOR = Color.BLACK;

	/**
	 * Table configuration
	 */
	float TABLE_ROW_HEIGHT = 15;
	float TABLE_CELL_PADDING = 2;
	float TABLE_COLUMN_HEIGHT = TABLE_ROW_HEIGHT;

	void export(String fileName) throws IOException;

	Header headerConfiguration();

	List<Column> columnConfiguration();

	List<Range> fixedColumnRangeConfiguration();

	List<List<String>> populateData();

	default Footer footerConfiguration() {
		URL resource = Driver.class.getClassLoader().getResource("images/logo.png");
		assert resource != null;
		return Footer.builder()
				.lineColor(PAGE_FOOTER_LINE_COLOR)
				.lineWidth(PAGE_FOOTER_LINE_FONT_SIZE)
				.pageNumberPhrase(PAGE_FOOTER_PAGE_NUMBER_PHRASE)
				.pageNumberPhraseColor(PAGE_FOOTER_PAGE_NUMBER_PHRASE_COLOR)
				.pageNumberPhraseFont(PAGE_FOOTER_PAGE_NUMBER_PHRASE_FONT)
				.pageNumberPhraseSize(PAGE_FOOTER_PAGE_NUMBER_PHRASE_FONT_SIZE)
				.logoImage(new File(resource.getPath()))
				.build();
	}

	Graph getGraphs();
}
