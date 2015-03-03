package com.imaginea.apps.crawler.workers.records;

/**
 * @author vamsi emani
 */
public class WorkerRecord {
	
	private Thread owner;
	
	public void setOwner(Thread thread){
		owner = thread;
	}
	
	public Thread getOwner(){
		return owner;
	}
	
	public String status(){
		return getOwner().getName();
	}
}
