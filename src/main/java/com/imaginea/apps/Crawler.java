package com.imaginea.apps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
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
	private List<HtmlAnchor> hyperlinks;		
	private List processedPages;
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
		try {						
			this.hyperlinks = getHyperlinks(getUrl());
			this.processedPages = new ArrayList();			
			this.processedPages.add("/mod_mbox/maven-users//"+getRelativeUrl());
			this.processedPages.add("/mod_mbox/maven-users//"+getRelativeUrl()+"?0");
		} catch (IOException e) {			
			e.printStackTrace();
		}				
	}

	public String getRelativeUrl(){
		return this.urlSuffix+"/thread";
	}
	
	public String getUrl(){
		return StringConstants.BASEURL+"/"+getRelativeUrl();
	}
	
	/** 
	 * Returns a list of mail links extracted from the webpage.  
	 */
	@Override
	public List<MailSeed> extractLinks() {		
		List<MailSeed> linksExtracted = new ArrayList<MailSeed>();
		for(HtmlAnchor anchor : hyperlinks){
			String href = anchor.getHrefAttribute();
			if(href.startsWith("%3c")){
				String decodedHref = Utility.decodeUrl(Utility.encodeUrl(href));				
				linksExtracted.add(MailSeed.newFor(decodedHref, this.urlSuffix));
			}
			else if(href.contains(getRelativeUrl()) && !processedPages.contains(href))
				try {
					processedPages.add(href);
					this.hyperlinks.addAll( ((HtmlPage) anchor.click()).getAnchors() );
				} catch (IOException e) {
					e.printStackTrace();
				}				
		}		
		return linksExtracted;
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
	public List<HtmlAnchor> getHyperlinks(String url) throws IOException{
		HtmlPage page = null;
		List anchors = null;
		try {
			page = webClient.getPage(url);			
			anchors = new CopyOnWriteArrayList<HtmlAnchor>(page.getAnchors());			 
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
		return validateInput(getRelativeUrl());
	}
	
	/**
	 * Validates whether the mail links for the specified month and year 
	 * exists on the main page or not. 
	 */
	public boolean validateInput(String relativeUrl) {	
		boolean isValid = false;
		try{
			HtmlPage mainPage = webClient.getPage(StringConstants.BASEURL);
			HtmlPage childPage = webClient.getPage(getUrl());			
			isValid = (mainPage != null) && (childPage != null);
			if(isValid)
				currentPage = childPage;			
		}catch(Exception e){
			log.severe("Incorrect input "+getUrl());
		}
		return isValid;			
	}

}
