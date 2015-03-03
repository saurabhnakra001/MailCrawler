package com.imaginea.apps;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author vamsi emani
 *
 * Subclass this and implement abstract methods
 * to provide your own implementation of the crawler.
 */

public abstract class AbstractCrawler implements WebSpider {
		
	protected SeedProcessor seedProcessor;			
	private static int default_download_worker_count = 1;
	private static int default_link_generate_worker_count = 1;
	private int download_worker_count = default_download_worker_count;
	private int link_generate_worker_count = default_link_generate_worker_count;

	
	public void crawl() {
		List<Link> pageLinks = collectHyperlinks();		
		generateLinks(pageLinks);
		download();
	}

	public abstract List<Link> collectHyperlinks();					
	
	public boolean canCrawl() {
		return true;
	}

	public void setDownloadWorkerCount(int num){
		this.download_worker_count = num;
	}
	
	public void setLinkGenerateWorkerCount(int num){
		this.link_generate_worker_count = num;
	}
	
	public int getLinkGenerateWorkerCount(){
		return this.link_generate_worker_count;
	}
	
	public int getDownloadWorkerCount(){
		return this.download_worker_count;
	}
	
	public void generateLinks(List<Link> pageLinks){
		seedProcessor.generateSeeds(link_generate_worker_count, pageLinks);
	}
	
	public void setProcessor(SeedProcessor processor){
		this.seedProcessor = processor;
	}
	
	public void download() {		
		seedProcessor.downloadSeeds(getDownloadWorkerCount()); 		
	}

}
