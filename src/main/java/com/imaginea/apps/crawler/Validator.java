package com.imaginea.apps.crawler;


/**
 * @author vamsi emani

 */
public class Validator {

	private AbstractMailCrawler crawler;

	public Validator(AbstractMailCrawler crawler) {
		this.crawler = crawler;
	}

	public boolean isValidPageLink(String href){
		return href.contains(crawler.getInputYear()) && href.contains("mbox")
				&& !href.contains("author") && !href.contains("date");
	}
	
	public boolean isValidMailLink(String href){
		return href.startsWith("%3");
	}	
	
}
