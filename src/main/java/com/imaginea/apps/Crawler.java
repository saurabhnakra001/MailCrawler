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
		
	WebClient webClient = null;	
	HtmlPage currentPage = null;
	private List<Link> hyperlinks = new CopyOnWriteArrayList<Link>();;		
	private List processedPages = new ArrayList();
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
	
	/**
	 * Process the web page to load the list of mail messages loaded by js. 
	 */
	@Override
	public void collectHyperlinks() {
		try {						
			HtmlPage page = webClient.getPage(getUrl());	
			HtmlPage resp = HTMLParser.parseHtml(page.getWebResponse(), webClient.getCurrentWindow());
			this.hyperlinks = addValidHyperlinks(resp);								
		} catch (IOException e) {			
			e.printStackTrace();
		}				
	}
	
	@Override
	public void processLinksOnWebPage() {
		Iterator<Link> it = hyperlinks.listIterator();				
		while(it.hasNext()){			
			Link link = it.next();
			String urlSuffix = link.getUrlSuffix();
			String href = link.href();	
			if(link.isPageLink()){
				//urlSuffix = href.substring(0, href.indexOf("/"));
				if(!processedPages.contains(urlSuffix)){
					try {
						processedPages.add(urlSuffix);
						Object next = link.anchor.click();
						if(next instanceof HtmlPage){
							HtmlPage nextResp = HTMLParser.parseHtml(((Page) next).getWebResponse(), webClient.getCurrentWindow());
							addValidHyperlinks((HtmlPage) nextResp);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			}
		}
		
	}
	
	public String getUrl(){
		return StringConstants.BASEURL;
	}
	
	/** 
	 * Returns a list of mail seeds extracted from the webpage.  
	 */
	@Override
	public List<MailSeed> extractSeeds() {		
		List<MailSeed> seeds = new ArrayList<MailSeed>();				
		Iterator<Link> it1 = hyperlinks.iterator();
		while(it1.hasNext()){
			Link link = it1.next();						
			if(link.isMailLink()){							
				String decodedHref = Utility.decodeUrl(Utility.encodeUrl(link.href()));				
				seeds.add(MailSeed.newFor(decodedHref, link.getUrlSuffix()));
			}
		}
		System.out.println(hyperlinks.size());
		return seeds;
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
	
	public void closeWebClient(){
		webClient.closeAllWindows();
	}
	
	/**
	* Adds the valid hyperlinks on page. 
	*/
	public List<Link> addValidHyperlinks(HtmlPage page) throws IOException{				
		for (HtmlAnchor anchor : page.getAnchors()) {
			String href = anchor.getHrefAttribute();
			if(Utility.isValidPageLink(href)){
				String urlSuffix = Utility.urlSuffixOfUrl(href);
				if(!processedPages.contains(urlSuffix)){
					Link l = Link.newPageLink(anchor);
					log.info("Processed link : "+l);
					hyperlinks.add(l);
				}
			}
			else if(Utility.isValidMailLink(href))
				hyperlinks.add(Link.newMailLink(anchor));
		}			
		return hyperlinks;	
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
