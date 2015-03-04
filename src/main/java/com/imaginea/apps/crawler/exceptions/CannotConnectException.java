package com.imaginea.apps.crawler.exceptions;

/**
 * @author vamsi emani
 */
public class CannotConnectException extends CrawlerException{
	
	public CannotConnectException(String msg) {
		super(msg);
	}
	
	public CannotConnectException(Throwable e) {
		super(e);
	}

	public CannotConnectException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
