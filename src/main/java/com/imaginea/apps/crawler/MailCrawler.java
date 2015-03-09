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
import static com.imaginea.apps.crawler.StringConstants.CHECK_URL_OR_INTERNET_CONNECTION;
import static com.imaginea.apps.crawler.StringConstants.CANNOT_COLLECT_LINKS;;

/** 
 * @author vamsi emani
 * 
 * A maven mail chain crawler that extracts links from page source and downloads the mails.
 * Refer http://mail-archives.apache.org/mod_mbox/maven-users
 */

public class MailCrawler extends AbstractMailCrawler {
		
	private WebClient webClient = null;				
	private static final Logger log = Logger.getLogger(MailCrawler.class);	
				
	/**
	 * Process the web page to load the list of mail messages loaded by js. 
	 */
	
	public Queue<Link> collectHyperlinks() {	
		Queue<Link> pageLinks = new LinkedBlockingQueue<Link>();
		try {	
			log.info("Collecting hyperlinks on : "+getUrl());
			HtmlPage page = webClient.getPage(getUrl());	
			HtmlPage resp = HTMLParser.parseHtml(page.getWebResponse(), webClient.getCurrentWindow());
			pageLinks = processPage(resp, pageLinks);								
		} catch (IOException e) {			
			log.error(CANNOT_COLLECT_LINKS, e);
		}					
		return pageLinks;
	}

	private Queue<Link> processPage(HtmlPage page, Queue<Link> links){
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
	 * A headless browser that provides ability to 
	 * execute browser js ..
	 */
	public void initializeWebClient(){
		Config conf = new Config().load();		
		Helper help = getHelper();
		webClient = new WebClient(BrowserVersion.CHROME);		
		webClient.getOptions().setTimeout(conf.timeout());		
		webClient.waitForBackgroundJavaScript(conf.waitForBackgroundJavaScript());
		webClient.getOptions().setRedirectEnabled(conf.redirectedEnabled());		
		webClient.getOptions().setJavaScriptEnabled(conf.javascriptEnabled());
		webClient.getOptions().setCssEnabled(conf.cssEnabled()); 
		webClient.getOptions().setUseInsecureSSL(conf.useInsecureSSL());			    		    
	   // webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	    //webClient.getOptions().setThrowExceptionOnScriptError(false);	    	    
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
	private boolean validateInput(String relativeUrl) {
		boolean isValid = false;
		try {
			isValid = webClient.getPage(getUrl()) != null;
		}catch(UnknownHostException e){
			log.error(CHECK_URL_OR_INTERNET_CONNECTION);
		}
		catch (Exception e) {		
			log.error(e);
		} 
		return isValid;
	}

}
