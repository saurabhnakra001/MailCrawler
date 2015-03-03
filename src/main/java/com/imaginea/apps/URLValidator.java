package com.imaginea.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author vamsi emani

 */
public class URLValidator {

	public boolean isValidPageLink(String href){
		return href.contains(StringConstants.INPUT_YEAR) && href.contains("mbox")
				&& !href.contains("author") && !href.contains("date");
	}
	
	public boolean isValidMailLink(String href){
		return href.startsWith("%3");
	}	

	public List<Link> getValidPagelinks(HtmlPage page, List<Link> links){
		for (HtmlAnchor anchor : page.getAnchors()) {
			String href = anchor.getHrefAttribute();
			if(isValidPageLink(href)){
				String urlSuffix = Utility.urlSuffixOfUrl(href);				
				Link link = new Link(anchor, Link.LinkType.PAGE);
				if(!links.contains(link)){
					System.out.println("Processed link : "+link);
					links.add(link);
				}
			}
		}			
		return links;
	}
	/**
	* Gets the valid hyperlinks on page. 
	*/
	public List<Link> getValidMaillinks(HtmlPage page, List<Link> links) throws IOException{		
//		List<Link> links = new ArrayList<Link>();		
		for (HtmlAnchor anchor : page.getAnchors()) {
			String href = anchor.getHrefAttribute();
			if(isValidMailLink(href)){
				Link aLink = new Link(anchor, Link.LinkType.MAIL);
				if(!links.contains(aLink)){
					System.out.println("Processed link : "+aLink);
					links.add(aLink);
				}
			}
		}			
		return links;	
	}
}
