package com.imaginea.apps.crawler.exceptions;

/**
 * @author vamsi emani
 */
public class ConnectException extends CrawlerException{
	
	public ConnectException(String msg) {
		super(msg);
	}
	
	public ConnectException(Throwable e) {
		super(e);
	}

	public ConnectException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
