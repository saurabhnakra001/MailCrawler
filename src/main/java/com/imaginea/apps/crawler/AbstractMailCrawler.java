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
		
	protected MailSeedProcessor processor;			
	private static int default_download_worker_count = 1;
	private static int default_link_generate_worker_count = 1;
	private int downloadWorkerCount = default_download_worker_count;
	private int linkGenerateWorkerCount = default_link_generate_worker_count;
	private Validator validator;
	private Helper helper;	
	private String url, inputYear;	

	public void crawl() {
		Queue<Link> pageLinks = collectHyperlinks();		
		generateSeeds(pageLinks);
		download();
	}
	
	public void generateSeeds(Queue<Link> pageLinks){
		getProcessor().generateSeeds(linkGenerateWorkerCount, pageLinks);
	}		
	
	public List<Future<WorkerRecord>> download() {		
		return getProcessor().downloadSeeds(getDownloadWorkerCount()); 		
	}

	public abstract Queue<Link> collectHyperlinks();					
	
	public boolean canCrawl() {
		return true;
	}

	public boolean canResume(){
		return getProcessor().isResumeState() && getProcessor().hasSeeds();
	}		
	
	public void resume(){		
		download();
	}
	
	public void setDownloadWorkerCount(int num){
		this.downloadWorkerCount = num;
	}
	
	public void setLinkGenerateWorkerCount(int num){
		this.linkGenerateWorkerCount = num;
	}
	
	public int getLinkGenerateWorkerCount(){
		return this.linkGenerateWorkerCount;
	}
	
	public int getDownloadWorkerCount(){
		return this.downloadWorkerCount;
	}
	
	public void setProcessor(MailSeedProcessor processor){
		this.processor = processor;
	}
	
	public MailSeedProcessor getProcessor(){
		return this.processor;
	}
	
	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	public Helper getHelper() {
		return helper;
	}

	public void setHelper(Helper helper) {
		this.helper = helper;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getInputYear() {
		return inputYear;
	}

	public void setInputYear(String inputYear) {
		this.inputYear = inputYear;
	}
		
}
