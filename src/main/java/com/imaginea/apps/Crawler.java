package com.imaginea.apps;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Scanner;

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
 */

public class Crawler implements WebSpider {

	JSSandbox sandbox = new JSSandbox();
	String urlSuffix;	
	WebClient webClient = null;	
	HtmlPage currentPage = null;
	Scanner scanner = new Scanner(System.in);
	SeedManager mgr = new SeedManager();
	
	public static void main(String[] args) throws IOException {
		Crawler crawler = new Crawler();
		crawler.initializeWebClient();
		crawler.consumeInputs();
		if(crawler.canCrawl())
			crawler.run();
		else
			System.out.println("Unable to run the crawler. Please verify inputs.");
		crawler.closeWebClient();
	}
	
	/**
	 * Reads input from command line.
	 */
	public void consumeInputs(){
		String year = readInput("Enter year in YYYY format (example - 2014) : ");		
		String month = readInput("Enter month in MM format (example - 02) : ");
		this.urlSuffix = year + month + ".mbox";				
	}
	
	public void run(){			
		initializeWebClient();		
		if(this.currentPage == null) return;
		HtmlPage pageWithMsgs =  sandbox.loadMessages(this.currentPage, urlSuffix); 		
		Utility.sleep(3000);
		HtmlTable msgsTable = (HtmlTable) pageWithMsgs.getHtmlElementById("msglist");		
		extractAndSeedLinks(pageWithMsgs);		
		// mgr.printStatus();
		// TODO : Place download action else where if possible. 
		mgr.downloadSeeds(); 			
	}
	
	/** 
	 * Parses the htmlpage's messages list table to extract mail links 
	 * and create seed objects out of the links
	 * For example : Refer http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/browser 
	 */
	public void extractAndSeedLinks(HtmlPage page){
		HtmlTable msgsTable = (HtmlTable) page.getHtmlElementById("msglist");	
		int MAIL_SUBJECT = 1;
		for (final HtmlTableRow row : msgsTable.getRows()) {	
		    HtmlTableCell cell = row.getCell(MAIL_SUBJECT);
		    for ( DomElement child : cell.getChildElements()){
		    	if(child instanceof HtmlAnchor)	{	    		
		    		String href = ((HtmlAnchor) child).getHrefAttribute();
		    		String decodedHref = sandbox.decodeURIComponent(page, href); 
		    		mgr.addNewSeed(decodedHref, this.urlSuffix);		    		
		    	}
		    }		    		    
		}	
	}	
	
	public String readInput(Object msg){
		System.out.println(msg);
		return scanner.nextLine();
	}
	
	/**
	 * Sort of a proxy that provides ability to 
	 * execute browser js without actually opening a browser..
	 */
	public void initializeWebClient(){
		webClient = new WebClient(BrowserVersion.CHROME);		
	}
	
	public void closeWebClient(){
		webClient.closeAllWindows();
	}
	
	/**
	* Gets the list of anchors on a given html page
	*/
	public List<HtmlAnchor> getAnchors(String url){
		HtmlPage mainPage = null;
		try {
			mainPage = webClient.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return mainPage.getAnchors();	
	}
	
	public boolean canCrawl(){
		return validateInput(urlSuffix+"/browser");
	}
	
	/**
	 * Validates whether the mail links for the specified month and year 
	 * exists on the main page or not. 
	 */
	public boolean validateInput(String relativeUrl) {			
		List<HtmlAnchor> anchors = getAnchors(StringConstants.BASEURL); 		
		/** Iterate over the anchors in the base url to check if page with link exists or not **/
		HtmlAnchor anchor = null;
		boolean found = false;
		for (int i = 0; i < anchors.size(); ++i){
		    anchor = anchors.get(i);
		    String href = anchor.getHrefAttribute();		    
		    if(href.equals(relativeUrl)){
		    	try {
					this.currentPage = anchor.click();
					//HtmlUtility.printPage(p);
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
