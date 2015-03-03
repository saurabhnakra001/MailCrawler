package com.imaginea.apps.crawler;


import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;

import com.imaginea.apps.crawler.processor.MailSeedProcessor;
import com.imaginea.apps.crawler.workers.records.WorkerRecord;

/**
 * 
 * @author vamsi emani
 *
 * Subclass this and implement abstract methods
 * to provide your own implementation of the crawler.
 */

public abstract class AbstractMailCrawler implements WebSpider {
		
	protected MailSeedProcessor seedProcessor;			
	private static int default_download_worker_count = 1;
	private static int default_link_generate_worker_count = 1;
	private int download_worker_count = default_download_worker_count;
	private int link_generate_worker_count = default_link_generate_worker_count;
	private Validator validator;;	

	public void crawl() {
		Queue<Link> pageLinks = collectHyperlinks();		
		generateSeeds(pageLinks);
		download();
	}
	
	public void generateSeeds(Queue<Link> pageLinks){
		getProcessor().generateSeeds(link_generate_worker_count, pageLinks);
	}		
	
	public List<Future<WorkerRecord>> download() {		
		return getProcessor().downloadSeeds(getDownloadWorkerCount()); 		
	}

	public abstract Queue<Link> collectHyperlinks();					
	
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
	
	public void setProcessor(MailSeedProcessor processor){
		this.seedProcessor = processor;
	}
	
	public MailSeedProcessor getProcessor(){
		return this.seedProcessor;
	}
	
	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
}
