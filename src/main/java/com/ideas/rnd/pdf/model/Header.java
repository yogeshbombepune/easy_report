package com.ideas.rnd.pdf.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Header {
	Map<String, Object> metaKeyVal;
	private String propertyName;
	private String reportName;
}
