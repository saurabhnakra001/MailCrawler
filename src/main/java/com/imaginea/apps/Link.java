package com.imaginea.apps;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

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
		if(linkType.equals(LinkType.MAIL)){
			String urlStr = url.getPage().getUrl().toString();	
			this.urlSuffix = Utility.urlSuffixOfUrl(urlStr);;
		}else if(linkType.equals(LinkType.PAGE)){
			this.urlSuffix = Utility.urlSuffixOfUrl(this.href());
		}		
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
	    	String objHref = ((Link) object).href();
	        isEqual = (this.href() == objHref) /*|| objHref.contains(this.href()) || this.href().contains(objHref)*/ ;
	    }

	    return isEqual;
	}
	
	@Override
	public int hashCode() {		
		return this.href().hashCode();		
	}
}
