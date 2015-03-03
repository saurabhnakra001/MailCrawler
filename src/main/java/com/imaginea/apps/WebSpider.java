package com.imaginea.apps;

/**
 * 
 * @author vamsi emani
 *
 */
public interface WebSpider {	
	
	/** This relates to how the crawler executes the extraction of links. **/
	public void crawl();
	
	/** Any other validations for crawler **/
	public boolean canCrawl();
	
	/** Implementation to download mail content in extracted link **/
	public void download();
}
