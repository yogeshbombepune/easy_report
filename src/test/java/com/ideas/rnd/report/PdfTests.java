package com.ideas.rnd.report;


import java.text.DecimalFormat;

public class PdfTests {

	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("#0.00");
		System.out.println(df.format(1155.55));
	}


}
