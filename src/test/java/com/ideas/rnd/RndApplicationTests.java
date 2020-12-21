package com.ideas.rnd;

import com.cluster.report.report.model.pdf.Column;
import com.google.gson.Gson;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RndApplicationTests {

    @Test
    public void contextLoads() {
    }

	@Test
	public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
		String generatedString = RandomStringUtils.random(10, true, true);
		System.out.println(generatedString);
	}

	@Test
	public void utfCheckTestForGson() {
		List<Column> columnList = new ArrayList<>();
		Column column = new Column();
		column.setName("X");
		columnList.add(column);
		column = new Column();
		column.setName("电子邮件");
		columnList.add(column);
		Gson gson = new Gson();
		System.out.println(gson.toJson(columnList));
	}

	@Test
	public void bufferedWriterTest() throws IOException {
		StringBuilder str = new StringBuilder();
		str.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" +
				"<head>\r\n" +
				"        <title></title>\r\n" +
				" <meta charset=\"utf-8\" />" +

				"</head>\r\n" +
				"<body>\r\n" +
				"<div><center><h6>你好中文</h6></center></div>" +
				"<div id=\"chartContainer\"></div>\r\n" +
				"</body>\r\n" +
				"</html>");
		BufferedWriter writer = new BufferedWriter(new FileWriter("test.html"));
		writer.write(str.toString());
		writer.close();
	}

}
