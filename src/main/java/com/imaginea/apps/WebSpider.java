package com.imaginea.apps;

/**
 * 
 * @author vamsi emani
 *
 */
public interface WebSpider {

	/** Enter inputs **/;
	public void consumeInputs();
	
	/** This relates to how the crawler executes the extraction of links. **/
	public void run();
	
	/** Any other validations for crawler **/
	public boolean canCrawl();
}
