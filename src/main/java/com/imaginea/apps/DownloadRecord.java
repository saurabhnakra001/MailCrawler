package com.imaginea.apps;

/**
 * 
 * @author vamsi emani
 * 
 * To print download statistics on the worker.
 *
 */
public class DownloadRecord {
	
	private int download = 0;
	private int failed = 0;
	private Thread owner;
	
	public void downloaded(){
		download++;
	}

	public void failed(){
		failed++;
	}
	
	public int totalProcessed(){
		return download+failed;
	}
	
	public String status(){
		return owner.getName()+" successfully downloaded "+download+"/"+(download+failed)+" downloads.";
	}
	
	public void setOwner(Thread thread){
		owner = thread;
	}
	
	public Thread getOwner(){
		return owner;
	}
}
