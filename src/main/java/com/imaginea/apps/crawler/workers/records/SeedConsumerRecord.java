package com.imaginea.apps.crawler.workers.records;

/**
 * 
 * @author vamsi emani
 * 
 * To print download statistics on the worker.
 *
 */
public class SeedConsumerRecord extends WorkerRecord{
	
	private int download = 0;
	private int failed = 0;
	
	public void downloaded(){
		download++;
	}

	public int getDownloadedCount(){
		return download;
	}
	
	public void failed(){
		failed++;
	}
	
	public int totalProcessed(){
		return download+failed;
	}
	
	public String status(){
		return super.status()+" successfully downloaded "+download+"/"+(download+failed)+" downloads.";
	}
		
}
