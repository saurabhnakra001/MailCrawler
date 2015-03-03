package com.imaginea.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author vamsi emani
 * Process links and Generate seeds and puts into the queue  
 */

public class LinkWorker implements Callable{

	private BlockingQueue<MailSeed> queue;
	private Queue<Link> pageLinks;
	private WebClient webClient;
	private ConcurrentHashMap<String, MailSeed> visited;
	
	public LinkWorker(BlockingQueue<MailSeed> queue, Queue<Link> links) {
		this.pageLinks = links;
		this.queue = queue;
	}
	
	public void setWebClient(WebClient webClient){
		this.webClient = webClient;
	}
	
	public void processLinks(Queue<Link> collected) {	
		Iterator<Link> it = collected.iterator();				
		while(it.hasNext()){			
			Link link = it.next();
			String urlSuffix = link.getUrlSuffix();
			String href = link.href();	
			if(link.isPageLink()){								
				try {						
					Object next = link.getAnchor().click();
					if(next instanceof HtmlPage){
						HtmlPage nextResp = HTMLParser.parseHtml(((Page) next).getWebResponse(), webClient.getCurrentWindow());
						createSeeds((HtmlPage) nextResp);
					}					
				} catch (IOException e) {
					e.printStackTrace();
				}					
			}
		}		
	}
	
	public void createSeeds(HtmlPage page) throws IOException{				
		for (HtmlAnchor anchor : page.getAnchors()) {
			String href = anchor.getHrefAttribute();
			if(new Validator().isValidMailLink(href) && visited.get(href) == null){												
				String urlStr = anchor.getPage().getUrl().toString();									
				String decodedHref = Utility.decodeUrl(Utility.encodeUrl(href));
				MailSeed seed = new MailSeed(decodedHref, Utility.urlSuffixOfUrl(urlStr));				
				queue.offer(seed);
				visited.put(href, seed);				
				System.out.println("Created seed : "+href);
			}
		}						
	}
	
	/**extract seeds logic here **/
	public Object call() throws Exception {	
		processLinks(pageLinks);;		
		return "Hello";
	}

	public void setVisited(ConcurrentHashMap<String, MailSeed> map) {		
		this.visited = map;
	}

}
