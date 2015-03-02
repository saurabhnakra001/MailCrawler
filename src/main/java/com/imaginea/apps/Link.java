package com.imaginea.apps;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Link {

	boolean isPageLink, isMailLink;
	
	String urlSuffix;
	
	HtmlAnchor anchor; 
	
	public Link(HtmlAnchor url) {
		this.anchor = url;		
	}
	
	public static Link newPageLink(HtmlAnchor url){
		Link aLink = new Link(url);						
		aLink.urlSuffix = Utility.urlSuffixOfUrl(aLink.href());;
		aLink.isPageLink = true;
		return aLink;
	}
	
	public static Link newMailLink(HtmlAnchor url){
		String urlStr = url.getPage().getUrl().toString();				
		Link aLink = new Link(url);
		aLink.isMailLink = true;
		aLink.urlSuffix = Utility.urlSuffixOfUrl(urlStr);;
		return aLink;
	}
	
	public String href(){
		return anchor.getHrefAttribute();
	}

	public boolean isPageLink(){
		return isPageLink;
	}
	
	public boolean isMailLink(){
		return isMailLink;
	}
	
	public String getUrlSuffix(){
		return urlSuffix;
	}
	
	@Override
	public String toString() {	
		if(urlSuffix != null && anchor != null)
			return urlSuffix+"("+href()+")";
		return super.toString();
	}
}
