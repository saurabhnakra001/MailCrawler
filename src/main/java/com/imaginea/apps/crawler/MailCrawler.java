package com.imaginea.apps.crawler;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;



import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.imaginea.apps.crawler.processor.MailSeedProcessor;

/** 
 * @author vamsi emani
 * 
 * A maven mail chain crawler that extracts links from page source and downloads the mails.
 * Refer http://mail-archives.apache.org/mod_mbox/maven-users
 */

public class MailCrawler extends AbstractMailCrawler {
		
	private WebClient webClient = null;				
	private static final Logger log = Logger.getLogger(MailCrawler.class);	
			
	public MailCrawler() {		
		this.setProcessor(new MailSeedProcessor(this));	
		this.setValidator(new Validator(this));
		this.setHelper(new Helper(this));
	}	
		
	/**
	 * Process the web page to load the list of mail messages loaded by js. 
	 */
	
	public Queue<Link> collectHyperlinks() {	
		Queue<Link> pageLinks = new LinkedBlockingQueue<Link>();
		try {	
			log.info(getUrl());
			HtmlPage page = webClient.getPage(getUrl());	
			HtmlPage resp = HTMLParser.parseHtml(page.getWebResponse(), webClient.getCurrentWindow());
			pageLinks = processPage(resp, pageLinks);								
		} catch (IOException e) {			
			e.printStackTrace();
		}					
		return pageLinks;
	}

	public Queue<Link> processPage(HtmlPage page, Queue<Link> links){
		for (HtmlAnchor anchor : page.getAnchors()) {
			String href = anchor.getHrefAttribute();
			if(getValidator().isValidPageLink(href)){
				String urlSuffix = getHelper().urlSuffixOfUrl(href);				
				Link link = new Link(anchor, Link.LinkType.PAGE);	
				link.setUrlSuffix(getHelper().urlSuffixOfUrl(link.href()));
				log.info("Processing : "+link);
				if(!links.contains(link))
					links.add(link);
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
		webClient.getOptions().setTimeout(20000);
	    webClient.waitForBackgroundJavaScript(60000);
	    webClient.getOptions().setRedirectEnabled(true);
	    webClient.getOptions().setJavaScriptEnabled(true);
	   // webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	    //webClient.getOptions().setThrowExceptionOnScriptError(false);
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
			isValid = webClient.getPage(getUrl()) != null;
		}catch(UnknownHostException e){
			log.error(StringConstants.CHECK_URL_OR_INTERNET_CONNECTION);
		}
		catch (Exception e) {		
			e.printStackTrace();
		} 
		return isValid;
	}

}
