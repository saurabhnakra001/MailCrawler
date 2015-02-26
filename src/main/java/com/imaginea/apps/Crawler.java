package com.imaginea.apps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
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
 * Also has the ability to invoke browser js to intrepret and extract mail links generated 
 * on the html page by ajax..   
 * 
 * Refer http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/browser 
 */

public class Crawler extends AbstractCrawler {

	JSSandbox sandbox = new JSSandbox();	
	WebClient webClient = null;	
	HtmlPage currentPage = null;				
	private static final Logger log = Logger.getLogger(Crawler.class.getName());
	
	public static void main(String[] args) throws IOException {		
		Crawler crawler = new Crawler();
		crawler.initializeWebClient();
		crawler.consumeInputs();
		if(crawler.canCrawl())
			crawler.run();
		else
			log.severe(StringConstants.CANNOT_CRAWL);
		crawler.closeWebClient();
	}			
	
	@Override
	public void consumeInputs() {
		super.consumeInputs();
		try{
			DOWNLOAD_WORKER_THREADS = Integer.parseInt(readInput(StringConstants.NUM_DOWNLOAD_WORKERS));
		}catch(NumberFormatException e){
			log.info(StringConstants.INVALID_NUM_WORKERS);
			DOWNLOAD_WORKER_THREADS = 1;
		}
	}
	
	/**
	 * Process the web page to load the list of mail messages loaded by js. 
	 */
	@Override
	public void processWebPage() {
		if(this.currentPage == null) 
			return;
		log.info("Loading mail messages on : "+currentPage);
		currentPage =  sandbox.loadMessages(this.currentPage, urlSuffix); 		
		Utility.sleep(3000);		
	}

	/** 
	 * Returns a list of mail links extracted from the webpage.  
	 */
	@Override
	public List<MailSeed> extractLinks() {	
		List<MailSeed> links = new ArrayList<MailSeed>();
		HtmlTable msgsTable = (HtmlTable) currentPage.getHtmlElementById("msglist");	
		int MAIL_SUBJECT = 1;
		for (final HtmlTableRow row : msgsTable.getRows()) {	
		    HtmlTableCell cell = row.getCell(MAIL_SUBJECT);
		    for ( DomElement child : cell.getChildElements()){
		    	if(child instanceof HtmlAnchor)	{	    		
		    		String href = ((HtmlAnchor) child).getHrefAttribute();
		    		String decodedHref = sandbox.decodeURIComponent(currentPage, href);
		    		if(decodedHref.startsWith("ajax"))
		    			links.add(MailSeed.newFor(decodedHref, this.urlSuffix));		    		
		    	}
		    }		    		    
		}	
		return links;
	}
	
	/**
	 * A headless browser (not a browser) that provides ability to 
	 * execute browser js ..
	 */
	public void initializeWebClient(){
		webClient = new WebClient(BrowserVersion.CHROME);		
	}
	
	public void closeWebClient(){
		webClient.closeAllWindows();
	}
	
	/**
	* Gets the list of anchors on a given html page
	 * @throws IOException 
	*/
	public List<HtmlAnchor> getAnchors(String url) throws IOException{
		HtmlPage mainPage = null;
		List anchors = null;
		try {
			mainPage = webClient.getPage(url);
			anchors = mainPage.getAnchors();
		}catch(UnknownHostException e){
			log.severe("Cannot reach " + e.getMessage());
			throw e;
		}catch (FailingHttpStatusCodeException e) {
			log.severe("Cannot reach " + e.getMessage());	
			throw e;
		} catch (MalformedURLException e) {
			log.severe("Incorrect url " + e.getMessage());	
			throw e;
		} catch (IOException e) {
			log.severe(e.getMessage());	
			throw e;
		}		
		return anchors;	
	}
	
	@Override
	public boolean canCrawl(){
		return validateInput(urlSuffix+"/browser");
	}
	
	/**
	 * Validates whether the mail links for the specified month and year 
	 * exists on the main page or not. 
	 */
	public boolean validateInput(String relativeUrl) {			
		List<HtmlAnchor> anchors = null; 
		try{
			anchors = getAnchors(StringConstants.BASEURL); 		
		}catch(Exception e){
			return false;
		}
		/** Iterate over the anchors in the base url to check if page with link exists or not **/
		HtmlAnchor anchor = null;
		boolean found = false;
		for (int i = 0; i < anchors.size(); ++i){
		    anchor = anchors.get(i);
		    String href = anchor.getHrefAttribute();		    
		    if(href.equals(relativeUrl)){
		    	try {
					this.currentPage = anchor.click();
					//Utility.printPage(p);
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
		    }
		}		
		return false;		
	}

}
