package com.imaginea.apps.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author vamsi emani

 */
public class Validator {

	public boolean isValidPageLink(String href){
		return href.contains(StringConstants.INPUT_YEAR) && href.contains("mbox")
				&& !href.contains("author") && !href.contains("date");
	}
	
	public boolean isValidMailLink(String href){
		return href.startsWith("%3");
	}	
	
}
