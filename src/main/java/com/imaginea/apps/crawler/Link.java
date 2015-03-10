package com.imaginea.apps.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

/**
 * @author vamsi emani
 */

public class Link {

	private String urlSuffix;	

	private HtmlAnchor anchor; 

	private LinkType linkType;
			
	public enum LinkType{
		PAGE, MAIL;
	}
				
	public Link(HtmlAnchor url, LinkType linkType) {		
		this.anchor = url;
		this.setLinkType(linkType);		
	}
	
	public String href(){
		return anchor.getHrefAttribute();
	}

	public boolean isPageLink(){
		return this.linkType == LinkType.PAGE;
	}
	
	public boolean isMailLink(){
		return this.linkType == LinkType.MAIL;
	}
	
	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}
	
	public String getUrlSuffix(){
		return urlSuffix;
	}
	
	public LinkType getLinkType() {
		return linkType;
	}
	
	public HtmlAnchor getAnchor() {
		return anchor;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}
	
	@Override
	public String toString() {	
		if(urlSuffix != null && anchor != null)
			return urlSuffix+"("+href()+")";
		return super.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		boolean isEqual= false;
	    if (object != null && object instanceof Link){
	    	Link obj = ((Link) object);	    	
	        isEqual = obj.toString().equals(this.toString());
	    }

	    return isEqual;
	}
	
	@Override
	public int hashCode() {		
		return this.href().hashCode();		
	}
	
}
