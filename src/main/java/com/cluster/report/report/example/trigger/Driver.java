package com.cluster.report.report.example.trigger;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import com.cluster.report.report.example.csv.ReportCsv;
import com.cluster.report.report.example.csv.ReportCsvImpl;
import com.cluster.report.report.example.excel.ReportExcel;
import com.cluster.report.report.example.excel.ReportExcelImpl;
import com.cluster.report.report.example.pdf.ReportPdf;
import com.cluster.report.report.example.pdf.ReportPdfImpl;

public class Driver {

	public static void main(String[] args) throws Exception {
		ReportPdf reportPdf = new ReportPdfImpl();
		reportPdf.export("results.pdf");
	}

	public static void main2(String[] args) throws Exception {
		ReportCsv reportCsv = new ReportCsvImpl();
		reportCsv.export("result.csv");
	}

	public static void main1(String[] args) throws Exception {
		ReportExcel reportExcel = new ReportExcelImpl();
		reportExcel.export("pcr.xlsx");
	}

	public static void main3(String[] args) throws Exception {

		Random random = new Random();

		Thread t1 = new Thread(() -> {
			try {
				for (int i = 0; i < 10; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException | IOException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			}
		});

		Thread t2 = new Thread(() -> {
			try {
				for (int i = 0; i < 15; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException | IOException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			}
		});

		Thread t3 = new Thread(() -> {
			try {
				for (int i = 0; i < 8; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		Thread t4 = new Thread(() -> {
			try {
				for (int i = 0; i < 2; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread t5 = new Thread(() -> {
			try {
				for (int i = 0; i < 5; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread t6 = new Thread(() -> {
			try {
				for (int i = 0; i < 50; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});


		Thread t7 = new Thread(() -> {
			try {
				for (int i = 0; i < 25; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread t8 = new Thread(() -> {
			try {
				for (int i = 0; i < 10; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread t9 = new Thread(() -> {
			try {
				for (int i = 0; i < 10; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		Thread t10 = new Thread(() -> {
			try {
				for (int i = 0; i < 10; i++) {
					int number = random.nextInt();
					long time = new Date().getTime();
					ReportExcel reportExcel = new ReportExcelImpl();
					reportExcel.export("result" + time + number + ".xlsx");
				}
			} catch (InterruptedException e) {
				// handle: log or throw in a wrapped RuntimeException
				throw new RuntimeException("InterruptedException caught in lambda", e);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});


		t1.start();

		t2.start();

		t3.start();

		t4.start();

		t5.start();
		t6.start();
		t7.start();
		t8.start();
		t9.start();
		t10.start();



	}
}
