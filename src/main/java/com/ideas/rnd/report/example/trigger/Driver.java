package com.ideas.rnd.report.example.trigger;

import com.ideas.rnd.report.example.csv.ReportCsv;
import com.ideas.rnd.report.example.csv.ReportCsvImpl;
import com.ideas.rnd.report.example.pdf.ReportPdf;
import com.ideas.rnd.report.example.pdf.ReportPdfImpl;

public class Driver {

	public static void main1(String[] args) throws Exception {
		ReportPdf reportPdf = new ReportPdfImpl();
		reportPdf.export("result.pdf");
	}

	public static void main(String[] args) throws Exception {
		ReportCsv reportPdf = new ReportCsvImpl();
		reportPdf.export("result.csv");
	}
}
