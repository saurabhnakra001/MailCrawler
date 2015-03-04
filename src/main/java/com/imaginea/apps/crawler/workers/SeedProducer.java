package com.imaginea.apps.crawler.workers;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.imaginea.apps.crawler.Link;
import com.imaginea.apps.crawler.MailSeed;
import com.imaginea.apps.crawler.StringConstants;
import com.imaginea.apps.crawler.Utility;
import com.imaginea.apps.crawler.Validator;
import com.imaginea.apps.crawler.exceptions.CannotConnectException;
import com.imaginea.apps.crawler.exceptions.CrawlerException;
import com.imaginea.apps.crawler.workers.records.SeedConsumerRecord;
import com.imaginea.apps.crawler.workers.records.SeedProducerRecord;
import com.imaginea.apps.crawler.workers.records.WorkerRecord;

/**
 * @author vamsi emani
 * Process links and Generate seeds and puts into the queue  
 */

public class SeedProducer implements Callable<WorkerRecord>{

	private BlockingQueue<MailSeed> queue;
	private Queue<Link> pageLinks;
	private WebClient webClient;
	private ConcurrentHashMap<String, MailSeed> visited;
	private WorkerRecord record;
	private Logger log = Logger.getLogger(SeedProducer.class.getName());
	
	public SeedProducer(BlockingQueue<MailSeed> queue, Queue<Link> links) {
		this.pageLinks = links;
		this.queue = queue;
		this.record = new SeedProducerRecord();
	}
	
	public void setWebClient(WebClient webClient){
		this.webClient = webClient;
	}
	
	public void processLinks(Queue<Link> collected) throws CrawlerException {	
		Iterator<Link> it = collected.iterator();
		try{
			while(it.hasNext()){			
				Link link = it.next();
				String urlSuffix = link.getUrlSuffix();
				String href = link.href();	
				if(link.isPageLink()){																	
					Object next = link.getAnchor().click();
					if(next instanceof HtmlPage){
						HtmlPage nextResp = HTMLParser.parseHtml(((Page) next).getWebResponse(), webClient.getCurrentWindow());
						createSeeds((HtmlPage) nextResp);
					}														
				}
			}
		}catch(RuntimeException | IOException e){
			if(e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof UnknownHostException)
				throw new CannotConnectException(StringConstants.CHECK_INTERNET_CONNECTION, e.getCause());
			else
				throw new CrawlerException(e);
		} 
	}
	
	public void createSeeds(HtmlPage page){				
		for (HtmlAnchor anchor : page.getAnchors()) {
			String href = anchor.getHrefAttribute();
			if(new Validator().isValidMailLink(href) && visited.get(href) == null){												
				String urlStr = anchor.getPage().getUrl().toString();									
				String decodedHref = Utility.decodeUrl(Utility.encodeUrl(href));
				MailSeed seed = new MailSeed(decodedHref, Utility.urlSuffixOfUrl(urlStr));				
				boolean success = queue.offer(seed);
				if(success)
					((SeedProducerRecord) record).seed();
				visited.put(href, seed);				
				log.info("Created seed : "+href);
			}
		}						
	}
	
	/**extract seeds logic here 
	 * @throws CrawlerException **/
	public WorkerRecord call() throws CrawlerException{			
		record.setOwner(Thread.currentThread());
		processLinks(pageLinks);;		
		return record;
	}

	public void setVisited(ConcurrentHashMap<String, MailSeed> map) {		
		this.visited = map;
	}

}
