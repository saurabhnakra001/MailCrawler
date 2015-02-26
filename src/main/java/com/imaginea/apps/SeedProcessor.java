package com.imaginea.apps;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * @author vamsi emani
 * 
 * Controls the processing of seeds with a queue.
 */
public class SeedProcessor {
	
	/** Use a blocking queue to enable multi-threading in future and to withstand internet connection loss **/
	LinkedBlockingQueue<MailSeed> queue = new LinkedBlockingQueue<MailSeed>();
	
	private static final Logger log = Logger.getLogger(SeedProcessor.class.getSimpleName());
	
	public void addSeed(MailSeed seed){
		queue.offer(seed);		
	}
	
	public void addSeeds(List<MailSeed> seeds){
		for(MailSeed seed : seeds)
			addSeed(seed);
	}
	
	/**
	 * Downloads the seeds in queue.. 
	 */
	public void downloadSeeds(int number_of_workers){
		for( int i = 0 ; i < number_of_workers ; i++)		
			new Thread(new DownloadWorker(queue)).start();		
	}
	
	public void printStatus(){
		System.out.println("seeds : "+ queue);
	}
}
