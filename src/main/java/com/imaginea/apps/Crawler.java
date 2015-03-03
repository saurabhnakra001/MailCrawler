package com.imaginea.apps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import net.sourceforge.htmlunit.corejs.javascript.TopLevel;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

/** 
 * @author vamsi emani
 * 
 * A maven mail chain crawler that extracts links from page source and downloads the mails.
 * Refer http://mail-archives.apache.org/mod_mbox/maven-users
 */

public class Crawler extends AbstractCrawler {
		
	private WebClient webClient = null;				
	private static final Logger log = Logger.getLogger(Crawler.class.getName());	
	private URLValidator validator = new URLValidator();
	
	/*public static void main(String[] args) throws IOException {		
		Crawler crawler = new Crawler();
		crawler.initializeWebClient();
		crawler.consumeInputs();
		if(crawler.canCrawl())
			crawler.crawl();
		else
			log.severe(StringConstants.CANNOT_CRAWL);
		crawler.closeWebClient();
	}	*/			
	
	public Crawler() {		
		this.seedProcessor = new MailSeedProcessor(this);		
	}
	
/*	public String readInput(Object msg){
		Scanner scanner = new Scanner(System.in);
		System.out.println(msg);
		return scanner.nextLine();
	}*/
	
	/**
	 * Reads input from command line.
	 */
	/*public void consumeInputs(){
		try{
			setLinkGenerateWorkerCount(Integer.parseInt(readInput(StringConstants.NUM_LINK_GENERATE_WORKERS)));
			setDownloadWorkerCount(Integer.parseInt(readInput(StringConstants.NUM_DOWNLOAD_WORKERS)));			
		}catch(NumberFormatException e){
			log.info(StringConstants.INVALID_NUM_WORKERS);					
		}
	}*/
		
	/**
	 * Process the web page to load the list of mail messages loaded by js. 
	 */
	@Override
	public List<Link> collectHyperlinks() {
		List<Link> links = new ArrayList<Link>() ;
		try {	
			log.info(getUrl());
			HtmlPage page = webClient.getPage(getUrl());	
			HtmlPage resp = HTMLParser.parseHtml(page.getWebResponse(), webClient.getCurrentWindow());
			links = validator.getValidPagelinks(resp, links);								
		} catch (IOException e) {			
			e.printStackTrace();
		}				
		return links;
	}

	public String getUrl(){
		return StringConstants.BASEURL;
	}
		
	/**
	 * A headless browser (not a browser) that provides ability to 
	 * execute browser js ..
	 */
	public void initializeWebClient(){
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setTimeout(120000);
	    webClient.waitForBackgroundJavaScript(60000);
	    webClient.getOptions().setRedirectEnabled(true);
	    webClient.getOptions().setJavaScriptEnabled(true);
	    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	    webClient.getOptions().setThrowExceptionOnScriptError(false);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setUseInsecureSSL(true);
	}
	
	public WebClient getWebClient(){
		return webClient;
	}
	
	public void closeWebClient(){
		webClient.closeAllWindows();
	}	
			
	@Override
	public boolean canCrawl(){
		return validateInput(getUrl());
	}
	
	/**
	 * Validates whether the mail links for the specified month and year 
	 * exists on the main page or not. 
	 */
	public boolean validateInput(String relativeUrl) {
		boolean isValid = false;
		try {
			isValid = webClient.getPage(StringConstants.BASEURL) != null;
		} catch (Exception e) {		
			e.printStackTrace();
		} 
		return isValid;
	}

}
