package com.imaginea.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class LinkWorker implements Callable{

	private BlockingQueue<MailSeed> queue;
	private List<Link> pageLinks;
	private WebClient webClient;
	
	public LinkWorker(BlockingQueue<MailSeed> queue, List<Link> links) {
		this.pageLinks = links;
		this.queue = queue;
	}
	
	public void setWebClient(WebClient webClient){
		this.webClient = webClient;
	}
	
	public void processLinks(List<Link> collected) {	
		Iterator<Link> it = collected.listIterator();				
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
			if(new URLValidator().isValidMailLink(href)){
				Link aLink = new Link(anchor, Link.LinkType.MAIL);
				if(!queue.contains(aLink)){
					System.out.println("Processed link : "+aLink);
					String decodedHref = Utility.decodeUrl(Utility.encodeUrl(aLink.href()));				
					queue.offer(MailSeed.newFor(decodedHref, aLink.getUrlSuffix()));
				}
			}
		}						
	}
	
	/**extract seeds logic here **/
	public Object call() throws Exception {	
		processLinks(pageLinks);;		
		return "Hello";
	}

}
