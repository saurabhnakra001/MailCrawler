package com.imaginea.apps.crawler;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.bcel.generic.RETURN;

import com.imaginea.apps.crawler.workers.records.WorkerRecord;

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
	
	/** 
	 * Implementation to download mail content in extracted link.
	 * returns downloaded file count
	 **/	 
	public int download();
}
