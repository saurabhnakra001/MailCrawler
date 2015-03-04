package com.imaginea.apps.crawler.exceptions;

/**
 * Generic exception for crawler. 
 * @author vamsi emani
 *
 */
public class CrawlerException extends Exception{

	public CrawlerException(String msg) {
		super(msg);
	}
	
	public CrawlerException(Throwable e) {
		super(e);
	}
	
	public CrawlerException(String msg, Throwable e) {
		super(msg, e);
	}
}
