package com.imaginea.apps.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;



import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import static com.imaginea.apps.crawler.StringConstants.ENCODE_ERROR;
import static com.imaginea.apps.crawler.StringConstants.DECODE_ERROR;
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
