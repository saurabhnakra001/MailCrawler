package com.imaginea.apps.crawler;

import static com.imaginea.apps.crawler.StringConstants.ERRORS.DECODE_ERROR;
import static com.imaginea.apps.crawler.StringConstants.ERRORS.ENCODE_ERROR;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;
/**
 * @author vamsi emani
 * Utility and helper methods here. 
 */
public class Helper {
	
	private AbstractMailCrawler crawler;
	
	private static Logger log = Logger.getLogger(Helper.class.getName());
	
	public Helper(AbstractMailCrawler crawler) {
		this.crawler = crawler;
	}
	
	public String decodeUrl(String encodedUrl){
		String result = null;
		try {
			result = java.net.URLDecoder.decode(encodedUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(DECODE_ERROR);
		}
		return result;
	}
	
	public String encodeUrl(String url){
		String result = null;
		try {
			result = java.net.URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {			
			log.error(ENCODE_ERROR);
		}
		return result;
	}
			
	public String urlSuffixOfUrl(String href){
		String urlSuffix = href.substring(href.indexOf(crawler.getInputYear()));		
		urlSuffix = urlSuffix.substring(0, urlSuffix.indexOf("/"));
		return urlSuffix;
	}	
		
}
